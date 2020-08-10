import com.collibra.dgc.core.api.dto.instance.asset.AddAssetRequest
import com.collibra.dgc.core.api.dto.instance.attribute.AddAttributeRequest
import com.collibra.dgc.core.api.dto.workflow.StartWorkflowInstancesRequest
import com.collibra.dgc.core.api.model.workflow.WorkflowBusinessItemType

       
loggerApi.info("-----------Workflow Started---------")
      
loggerApi.info("Note "+note.toString())
loggerApi.info("Definition "+definition.toString())
loggerApi.info("Name "+Signifier)

def newAssetUuid = assetApi.addAsset(AddAssetRequest.builder().name(Signifier)
    .displayName(Signifier)
    .typeId(conceptType)
    .domainId(string2Uuid(intakeVocabulary))
    .build()).getId()
        
addAttributeToAsset(newAssetUuid,definition,definitionAttributeTypeUUID)
addAttributeToAsset(newAssetUuid,note,noteAttributeTypeUUID)       

execution.setVariable("outputCreatedTermId",uuid2String(newAssetUuid))
loggerApi.info("Asset ID "+outputCreatedTermId)

def addAttributeToAsset(assetUuid,attributeValue,attributeTypeUuid) {
    if (attributeValue == null){
        return;
    }
    attributeApi.addAttribute(AddAttributeRequest.builder()
            .assetId(assetUuid)
            .typeId(string2Uuid(attributeTypeUuid))
            .value(attributeValue.toString())
            .build())
}

def setProperties =[:]
setProperties.put("newNote",note.toString())
setProperties.put("newDefinition",definition.toString())
setProperties.put("name",Signifier.toString())
setProperties.put("sourceId",outputCreatedTermId)
loggerApi.info('------Properties Set Successfully-------')

def list=[]
list.add(string2Uuid(outputCreatedTermId))
    
workflowInstanceApi.startWorkflowInstances(StartWorkflowInstancesRequest.builder()
    .workflowDefinitionId(string2Uuid("7eb1651f-ca6a-4853-b421-c690e6ed3970"))
    .businessItemType(WorkflowBusinessItemType.ASSET)
    .businessItemIds(list)
    .guestUserId(string2Uuid("7d415542-718c-403e-bde1-ad6c286f9522"))
    .formProperties(setProperties).build())

loggerApi.info("----------------Workflow Call Successful----------------")