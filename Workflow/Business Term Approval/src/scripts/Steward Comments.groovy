/*Add Comments V 1.0
Author: N. Padma Gokul
Description: This script is used to add the comments to 
the asset.
*/

import com.collibra.dgc.core.api.model.ResourceType   
import com.collibra.dgc.workflow.api.exception.WorkflowException

comments = technicalStewardComment.toString()
loggerApi.info("--Comments--"+comments)

if(comments.isEmpty()){
    loggerApi.error("Please provide comments.")
    String errorMessage = translation.getMessage("Please provide comments.")
    String errorTitle = translation.getMessage("commentsNotFound");
    WorkflowException workflowException = new WorkflowException(errorMessage);
    workflowException.setTitleMessage(errorTitle);
    throw workflowException;
}
else{
	commentApi.addComment(builders.get("AddCommentRequest")
        .baseResourceId(string2Uuid(assetId))
        .baseResourceType(ResourceType.Asset)
        .content(technicalStewardComment.toString()).build())
}            