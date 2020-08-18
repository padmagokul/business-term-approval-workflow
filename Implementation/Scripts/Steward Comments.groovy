/*Add Comments V 1.0
Author: N. Padma Gokul
Description: This script is used to add the comments to 
the asset.
*/

import com.collibra.dgc.core.api.model.ResourceType   


commentApi.addComment(builders.get("AddCommentRequest")
        .baseResourceId(string2Uuid(assetId))
        .baseResourceType(ResourceType.Asset)
        .content(technicalStewardComment.toString()).build())

            