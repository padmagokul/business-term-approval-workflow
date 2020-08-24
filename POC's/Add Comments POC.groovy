import com.collibra.dgc.core.api.dto.instance.comment.AddCommentRequest

    def usercomment = execution.getVariable("comment")

    def comments = AddCommentRequest.builder().content(usercomment.toString()).baseResourceId(item.id).baseResourceType(item.type)
    commentApi.addComment(comments.build())
        
    import com.collibra.dgc.core.api.model.ResourceType   
commentApi.addComment(builders.get("AddCommentRequest")
            .baseResourceId(string2Uuid(assetId))
            .baseResourceType(ResourceType.Asset)
            .content(technicalStewardComment.toString())
            .build()
        )

            