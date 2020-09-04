/*
Script Name: Mark as accepted Script
Author: N. Padma Gokul
Version: v1.1
Version History: Implemented the way to get status ID by configuration variable
Purpose: This script is used to change the status of the asset
*/

import com.collibra.dgc.workflow.api.exception.WorkflowException


try{
    def acceptedStatusId = statusApi.getStatusByName(acceptedStatus).getId()
    //Method call
    changeStatus(assetId, acceptedStatusId)
}
catch(Exception e){
    String errorMessage = translation.getMessage("Status: "+acceptedStatus+" not found")
    String errorTitle = translation.getMessage("statusNotFound");
    WorkflowException workflowException = new WorkflowException(errorMessage);
    workflowException.setTitleMessage(errorTitle);
    throw workflowException;
}

//Move the asset to target domain 
loggerApi.info("------Target domain id-----"+changedDomain) 
assetApi.changeAsset(builders.get("ChangeAssetRequest").id(string2Uuid(assetId)).domainId(changedDomain).build())

//User Defined methods start
//Method to change the status of the asset
def changeStatus(assetId, acceptedStatusId){
    assetApi.changeAsset(builders.get("ChangeAssetRequest").id(string2Uuid(assetId))
        .statusId(acceptedStatusId).build())
}