/*
Script Name: Add Acronym Relation Script
Author: N. Padma Gokul
Version: v1.2
Version History: v1.1 Added the method to get Technical and Business Steward users in single script.
                 v1.2 Changed the way of assigning users to task
Purpose: This script is used to add comments and acronym relation to the asset
*/

import com.collibra.dgc.core.api.model.ResourceType
import com.collibra.dgc.workflow.api.exception.WorkflowException

comments = analystComment.toString()
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
    //Add Comments to asset
    commentApi.addComment(builders.get("AddCommentRequest").baseResourceId(string2Uuid(assetId))
            .baseResourceType(ResourceType.Asset).content(analystComment.toString()).build())
    loggerApi.info('--------Comments added successfully----------')

    //Get Table UUID to show only table while adding table relations
    def tableUUID = assetTypeApi.findAssetTypes(builders.get("FindAssetTypesRequest").name(tableConceptType).build()).getResults()*.getId()
    loggerApi.info("Got Table UUID: "+tableUUID)
    execution.setVariable("tableID", tableUUID[0])

    //Find the users(ID's) that are responsible for the target domain with specified role
    loggerApi.info("----changed domain------"+changedDomain)
    def technicalStewardUser = responsibleUsers(changedDomain, technicalStewardRole)
    def businessStewardUser = responsibleUsers(changedDomain, businessStewardRole)

    //Build the user expression for user task
    def technicalStewardsUserList = []
    for(int user=0; user<technicalStewardUser.size; user++){
        technicalStewardsUserList.add("user("+technicalStewardUser[user]+")")
    }

    //Convert the list to CSV to use in user tasks
    def technicalStewardsUserCsv = utility.toCsv(technicalStewardsUserList)
    execution.setVariable('technicalStewards', technicalStewardsUserCsv)
    execution.setVariable('businessStewards', businessStewardUser)
    loggerApi.info('------Responsible user fetched successfully------')

    //Set the acronym asset to variable
    def relatedAsset = execution.getVariable("relatedAsset")
    
    //Get Acronym Relation UUID
    loggerApi.info("----Adding Acoronyms to BT-------")
    acronymRelationId = relationTypeApi.findRelationTypes(builders.get("FindRelationTypesRequest")
                                    .sourceTypeName(acronymRelationSourceType).targetTypeName(acronymRelationTargetType)
                                    .role(acronymRelation).build()).getResults()*.getId()
    loggerApi.info("-----Got acronym id------"+acronymRelationId)
    addRelationToAsset(assetId, acronymRelationId, relatedAsset)
}

    //User Defined method starts here
    //Method to add any acronym relation to asset
    def addRelationToAsset(def sourceUuid,def relationTypeUuid,def targetUuid) {
           if (targetUuid == null || targetUuid.isEmpty()){
                return;
           }
       
           relationApi.addRelation(builders.get("AddRelationRequest")
                   .sourceId(string2Uuid(sourceUuid)).targetId(targetUuid)
                   .typeId(relationTypeUuid).build())
        }

    //Method to find the users(ID's) that are responsible for the target domain with specified role
    def responsibleUsers(newDomain, roleName) {

        //Add the target domain to the list to get the users responsible for that domain
        def responsibilityList = []
        responsibilityList.add(newDomain)

        //Get Role ID for the given role
        def rolesIdList = roleApi.findRoles(builders.get('FindRolesRequest').name(roleName) 
                            .build()).getResults()*.getId()

        //Fetch the responsible user Id's
        def responsibleUserIds = responsibilityApi.findResponsibilities(builders.get('FindResponsibilitiesRequest')
                                    .resourceIds(responsibilityList).roleIds(rolesIdList) 
                                    .build()).getResults()*.getOwner()*.getId()
        loggerApi.info("-------------User IDs for the responsibility on target domain------------ "+responsibleUserIds)

        if(responsibleUserIds.isEmpty()){
            loggerApi.error("No users found for the specified role")
            String errorMessage = translation.getMessage("No users found for the specified role")
            String errorTitle = translation.getMessage("usersNotFound");
            WorkflowException workflowException = new WorkflowException(errorMessage);
            workflowException.setTitleMessage(errorTitle);
            throw workflowException;
        }

        def uuidList =[]
        for(userIds in responsibleUserIds){
            uuidList.add(uuid2String(userIds))
        }   
        def responsibleUserNames = userApi.findUsers(builders.get('FindUsersRequest').userIds(uuidList)
                                        .build()).getResults()*.getUserName()
        loggerApi.info("-------------User names for the responsibility on target domain------------ "+responsibleUserNames)
        return responsibleUserNames
    }