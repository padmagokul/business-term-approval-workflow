import com.collibra.dgc.core.api.dto.instance.comment.AddCommentRequest
import com.collibra.dgc.core.api.model.ResourceType

    commentApi.addComment(AddCommentRequest.builder().baseResourceId(string2Uuid(assetId))
            .baseResourceType(ResourceType.Asset)
            .content(adminComment.toString())
            .build())