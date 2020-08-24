/*
Script Name: Mark as under review Script
Author: N. Padma Gokul
Version: v1.1
Version History: Implemented the way to get status ID by configuration variable
Purpose: This script is used to change the status of the asset
*/

import com.collibra.dgc.workflow.api.exception.WorkflowException

//Method to change the status of the asset
def changeStatus(assetId, acceptedStatusId){
    def changedAsset = assetApi.changeAsset(builders.get("ChangeAssetRequest").id(string2Uuid(assetId))
        .statusId(acceptedStatusId).build())
}

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

