/*Add Comments and fetch responsible users script V 1.0
Author: N. Padma Gokul
Description: This script is used to add the comments to 
the asset and to find the responsible users for the specified domain.
*/

import com.collibra.dgc.core.api.dto.instance.comment.AddCommentRequest
import com.collibra.dgc.core.api.model.ResourceType


    //Method to add any acronym relation to asset
    def addAcronymRelationToAsset(assetId, acronymRelationId, relatedAsset) {
        if (relatedAsset == null || relatedAsset.isEmpty()){
            return
        }
        def addRelationRequests = []

        relatedAsset.each{ t ->
        loggerApi.info("Adding Acronym ID => ${t}")
        addRelationRequests.add(builders.get('AddRelationRequest').sourceId(string2Uuid(assetId))
            .targetId(t).typeId(string2Uuid(acronymRelationId)).build())
        }

        loggerApi.info("----------Relation Added successfully--------------------")
    }

    //Method to find the users(ID's) that are responsible for the target domain with specified role
    def responsibleUsers(responsibilityList, rolesList) {
        def responsibleUserIds = responsibilityApi.findResponsibilities(builders.get('FindResponsibilitiesRequest')
                                    .resourceIds(responsibilityList).roleIds(rolesList) 
                                    .build()).getResults()*.getOwner()*.getId()
        loggerApi.info("-------------User IDs for the responsibility on domain------------ "+responsibleUserIds)
        def uuidList =[]
        for(userIds in responsibleUserIds){
            uuidList.add(uuid2String(userIds))
        }   
        def responsibleUserNames = userApi.findUsers(builders.get('FindUsersRequest').userIds(uuidList)
                                        .build()).getResults()*.getUserName()
        loggerApi.info("-------------User names for the responsibility on domain------------ "+responsibleUserNames)
        return responsibleUserNames
    }

    loggerApi.info('---------start add comment---------')
    commentApi.addComment(AddCommentRequest.builder().baseResourceId(string2Uuid(assetId))
            .baseResourceType(ResourceType.Asset)
            .content(adminComment.toString())
            .build())
    loggerApi.info('--------Comments added successfully----------')

    def relatedAsset = execution.getVariable("relatedAsset")

    loggerApi.info("----Adding Acoronyms to BT-------")
    addAcronymRelationToAsset(assetId, acronymRelationId, relatedAsset)

    //Add the target domain to the list to get the users responsible for that domain
    def responsibilityList = []
    responsibilityList.add(domain)
    loggerApi.info('-------Target Domain Fetched------')

    //Get Role ID for the given role
    def rolesList = roleApi.findRoles(builders.get('FindRolesRequest').name('Technical Steward') 
                            .build()).getResults()*.getId()
    loggerApi.info('-----Got Role Id-------')

    //Find the users(ID's) that are responsible for the target domain with specified role
    def dgAnalyst = responsibleUsers(responsibilityList, rolesList)
    loggerApi.info('------Responsible user fetched successfully------')
