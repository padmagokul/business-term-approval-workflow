import com.collibra.dgc.core.api.dto.instance.comment.AddCommentRequest

    def usercomment = execution.getVariable("comments")

    def comments = AddCommentRequest.builder().content(usercomment.toString()).baseResourceId(item.id).baseResourceType(item.type)
    commentApi.addComment(comments.build())
        


            