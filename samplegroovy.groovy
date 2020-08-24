/*
Script Name: Input File and JSON Validation
Author: Lucid
Version: 1.0
Version History: Initial version
Purpose: Validates input CSV file and json to perform asset count reconciliation.
*/
import com.opencsv.CSVReader
import com.opencsv.CSVWriter
import java.io.Reader
import java.io.Writer
import org.apache.commons.io.FilenameUtils
import com.collibra.dgc.core.api.dto.instance.attachment.FindAttachmentsRequest
/*
------------------------------------------------------------
        Script To validate the file and JSON entry 
------------------------------------------------------------
Approach:
File Validation:
* Check if file attached to resource
* Check if file extension is valid
* Check if headers are as required
JSON Validation :
* Check if the string parses as a valid json object
* check if the Entries are present for the distinct asset types in the sheet

Configuration variables used:
* inputCsvFileId
* assetTypeIdsToBeMoved
*/

//Input File Validation
Reader inputStreamReader
Writer fileWriter
def headerValidationList =  ["Name", "Type", "Relation", "TargetType","RelationDirection", "TypeId", "RelationId", "TargetTypeId", "Count", "Domain", "Community", "SourceCheckParentAsset", "TargetCheckParentAsset","SourceBatchIdCheck","TargetBatchIdCheck", "RelationType" ]
def invalidJsonAssetTypes = []
def headerIndexMap = [:]
relationJSON = ""
defaultConfigString = "<<default>>"
logPrefix = "${workflowDefinitionName} - ${execution.getCurrentActivityId().capitalize()} -> "
execution.setVariable("errorFound",false)
/*
-------------------------------------------------------------
     Function : set Variables in case of workflow failed
-------------------------------------------------------------
*/
def exitPrep(String errorMessage)
{   
     return ["mailingErrorMessage":errorMessage, "workflowFailed":true, "errorOccured":true]
}

def summaryPrep(Map processCountMap){
    def logPrefixCopy = "${workflowDefinitionName} - Summary Export -> "
    def summaryText
    if(processCountMap) 
    {
        summaryText = "<b>Summary:</b></br>Loaded: ${processCountMap.loaded} Processed: ${processCountMap.processed} Passed: ${processCountMap.passed} Failed: ${processCountMap.failed}"
    }
    else{
        summaryText = "<b>Summary:</b></br>Loaded: 0 Processed: 0 Passed: 0 Failed: 0"
    }
    loggerApi.info(logPrefixCopy + "Exporting Reconciliation Summary...")
    return ["summaryText":summaryText]
}
/*
-----------------------------------------
            Mail Users Validation
-----------------------------------------
check if the users have mail ids in the profile
*/
def usersFromExpression = usersV2.getUsers(userExpressionForEmail) as List
usersFromExpression = usersFromExpression.findAll{ def email = it.getEmailAddress(); return (email != null && email != ""); }
def usersExists = (usersFromExpression != [])
execution.setVariable("userExpressionForEmail",usersFromExpression.collect{ "user(${it.getUserName()})" }.join(",") )
execution.setVariable("usersExists",usersExists)

/*
-----------------------------------------
        Get File from Attachment
-----------------------------------------
check if the loaded file is in attachment
if not attached throw an exception and stop execution
else retrieve the file ID
*/

// Check whether the input recieved is a file or an attachment and process accordingly
def inputFile
if(inputCsvAttachmentId == defaultConfigString){
    inputFile =  attachmentApi.findAttachments(FindAttachmentsRequest.builder().baseResourceId(itemV2.id).build()).getResults().find{it.getFile().getId().toString() == inputCsvFileId.toString()}?.getId() 
}
if(inputCsvFileId == defaultConfigString){
    inputFile = string2Uuid(inputCsvAttachmentId)
}
loggerApi.info("Input File Id =>> ${inputFile}")
if(inputFile == null)
{   
    execution.setVariables( exitPrep("File Not found in attachments") )
    execution.setVariables(summaryPrep(null))
    return
}
else{
    loggerApi.info(logPrefix+"File Imported: " + inputFile )
    execution.setVariable("inputFile",inputFile)
}
/*
-----------------------------------------
        Check Ids Passed 
-----------------------------------------
check if the UUIDs passed are valid
*/
def batchIdAttributeValid = true
if(batchIdAttributeTypeId != defaultConfigString){
    try{
        batchIdAttributeValid = attributeTypeApi.exists(string2Uuid(batchIdAttributeTypeId))
    }catch(e){
        batchIdAttributeValid = false
        loggerApi.error(logPrefix+"Batch Run ID Attribute validation failed")
    }
}else{
   batchIdAttributeValid = false
}
if(!batchIdAttributeValid){
    execution.setVariables(exitPrep("Batch Run Id Attribute Id Invalid"))
    execution.setVariables(summaryPrep(null))
    return
}
def partialLoadAttributeValid = true
if( updatePartialLoadAttributeFlag){
  if(partiallyLoadedAttributeTypeId != defaultConfigString){
    try{
     partialLoadAttributeValid = attributeTypeApi.exists(string2Uuid(partiallyLoadedAttributeTypeId))
    }catch(e){
        loggerApi.error(logPrefix+"Is Partially loaded Attribute validation failed")
        partialLoadAttributeValid = false
    }
  }else{
    partialLoadAttributeValid = false
  }
  if(!partialLoadAttributeValid){
      execution.setVariables(exitPrep("Partial Load Attribute Id Invalid"))
      execution.setVariables(summaryPrep(null))
      return
  }
}

/*
-----------------------------------------
        Check File format
-----------------------------------------
check if the loaded file is a spreadsheet
if not throw an exception and stop execution

*/
try
{
    importedFileName    = attachmentApi.getAttachment(inputFile).getFile().getName()

    if(FilenameUtils.isExtension(importedFileName,["xls","xlsx","csv"]))
    {
        loggerApi.info(logPrefix+"Uploaded file format is supported ")
    }
    else {
        loggerApi.error(logPrefix+"This Tool supports only speadsheets (.csv, .xls or .xlsx) ")
    }

    //csv File as Input Stream
    fileInputStream = attachmentApi.getAttachmentContent(inputFile)

    //Input Stream Reader
    inputStreamReader = new InputStreamReader(fileInputStream)
}
catch(e)
{
    execution.setVariables(exitPrep("Error in reading File " + e.getLocalizedMessage()))
    execution.setVariables(summaryPrep(null))
    return
}

reader = new CSVReader(inputStreamReader)
contentIterator = reader.iterator()

/*
-----------------------------------------
        JSON Validation
-----------------------------------------
check if the loaded JSON object is valid
if not throw an exception and stop execution

*/
try{
    relationJSON = new groovy.json.JsonSlurper().parseText(org.jsoup.Jsoup.parse(assetTypeIdsToBeMoved.toString()).text().toString())
}
catch(e)
{
    execution.setVariables(exitPrep("Passed Configuration JSON is Invalid " + e.getLocalizedMessage()))
    execution.setVariables(summaryPrep(null))
    return
}

/*
-----------------------------------------
            Header Validation
-----------------------------------------
check if the loaded file is a spreadsheet
if not throw an exception and stop execution

*/
header = contentIterator.next().toList()
headerIndexMap = [header,header.indices].transpose().collectEntries{[(it[0]):(it[1])]}
if (header != headerValidationList)
{
   execution.setVariables(exitPrep("Headers of the input file are not as specified in the Reconciliation workflow"))
    execution.setVariables(summaryPrep(null))
    return
}

/*
-----------------------------------------
        JSON Entry Validation
-----------------------------------------
check if the loaded JSON object is valid
if not throw an exception and stop execution

*/
while (contentIterator.hasNext())
{
    csvRecord = contentIterator.next()

    if (!relationJSON.(csvRecord[headerIndexMap.Type]))
    {

        if(!invalidJsonAssetTypes.contains(csvRecord[headerIndexMap.Type]))
        {
        invalidJsonAssetTypes.add(csvRecord[headerIndexMap.Type])
        }
        loggerApi.debug(logPrefix+"Added "+csvRecord[headerIndexMap.Type] +" to List")
    }
}
reader.close()
if(invalidJsonAssetTypes)
{   
    mailingErrorMessage = "Invalid JSON / entry not available for asset Types "+ invalidJsonAssetTypes.toString() 
    loggerApi.error(logPrefix+mailingErrorMessage )
    execution.setVariables(exitPrep(mailingErrorMessage))
    execution.setVariables(summaryPrep(null))
    return
}
else{
    loggerApi.info(logPrefix+"JSON Validated")
    execution.setVariable("errorOccured",false)
}
execution.setVariable("mailReconciliationResults", (mailReconciliationResults.toString().toLowerCase() == "true") )