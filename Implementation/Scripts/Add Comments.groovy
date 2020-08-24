/*
Script Name: Add Comments Script
Author: N. Padma Gokul
Version: v1.1
Version History: Changed the way of assigning users to task
Purpose: This script is used to add comments to the asset and get DG Analyst user.
*/

import com.collibra.dgc.core.api.model.ResourceType
import com.collibra.dgc.workflow.api.exception.WorkflowException


    //Add Comments to asset
    dgAdminComment = commentApi.addComment(builders.get("AddCommentRequest").baseResourceId(string2Uuid(assetId))
                        .baseResourceType(ResourceType.Asset).content(adminComment.toString()).build())
    loggerApi.info('--------Comments added successfully----------'+adminComment)

    //Get acronym UUID to show only acronyms while adding acronym relations
    def acronymUUID = assetTypeApi.findAssetTypes(builders.get("FindAssetTypesRequest").name(acronymConceptType).build()).getResults()*.getId()
    loggerApi.info("Got Acronym UUID: "+acronymUUID)
    execution.setVariable("acronymID", acronymUUID[0])

    //Add the target domain to the list to get the users responsible for that domain
    def targetDomain = []
    targetDomain.add(changedDomain)
    loggerApi.info('-------Target Domain Fetched------'+changedDomain)

    //Get Role ID for the given role
    def roleId = roleApi.findRoles(builders.get('FindRolesRequest').name(dgAnalystRole) 
                        .build()).getResults()*.getId()

    //Find the users(ID's) that are responsible for the target domain with specified role
    def responsibleUsers = responsibleUsers(targetDomain, roleId)
    def dgAnalystUserList = []

    //Build the user expression for user task
    for(int user=0; user<responsibleUsers.size;user++){
        dgAnalystUserList.add("user("+responsibleUsers[user]+")")
    }

    //Convert the list to CSV to use in user tasks
    dgAnalystUsers = utility.toCsv(dgAnalystUserList)
    loggerApi.info("--------CSV User-------"+dgAnalystUsers)

    execution.setVariable('dgAnalystUsers', dgAnalystUsers)
    loggerApi.info('------Responsible user fetched successfully------')

    //User defined methods start here
    //Method to find the users(ID's) that are responsible for the target domain with specified role
    def responsibleUsers(targetDomain, roleId) {
        def responsibleUserIds = responsibilityApi.findResponsibilities(builders.get('FindResponsibilitiesRequest')
                                    .resourceIds(targetDomain).roleIds(roleId) 
                                    .build()).getResults()*.getOwner()*.getId()
        loggerApi.info("-------------User IDs for the responsibility on domain------------ "+responsibleUserIds)

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
        loggerApi.info("-------------User names for the responsibility on domain------------ "+responsibleUserNames)
        return responsibleUserNames
    }