
def userIds = users.getUserIds("${stewardUserExpression}");
    if (userIds.isEmpty()){
        loggerApi.warn("No users to send a mail to, no mail will be send");
    } else {
        mail.sendMails(userIds, "assetCreated", null, execution);
    }
    