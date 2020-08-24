/*
Script Name: Add Acronym Relation Script
Author: N. Padma Gokul
Version: v1.1
Version History: Added the method to get Technical and Business Steward users in single script.
Purpose: This script is used to add comments and acronym relation to the asset
*/

import com.collibra.dgc.core.api.dto.instance.comment.AddCommentRequest
import com.collibra.dgc.core.api.model.ResourceType


    //Add Comments to asset
    loggerApi.info('---------start add comment---------')
    commentApi.addComment(builders.get("AddCommentRequest").baseResourceId(string2Uuid(assetId))
            .baseResourceType(ResourceType.Asset).content(analystComment.toString()).build())
    loggerApi.info('--------Comments added successfully----------')

    //Get Table UUID to show only table while adding table relations
    def tableUUID = assetTypeApi.findAssetTypes(builders.get("FindAssetTypesRequest").name(tableConceptType).build()).getResults()*.getId()
    loggerApi.info("Got Table UUID: "+tableUUID)
    execution.setVariable("tableID", tableUUID[0])

    def relatedAsset = execution.getVariable("relatedAsset")
    
    //Get Acronym Relation UUID
    loggerApi.info("----Adding Acoronyms to BT-------")
    acronymRelationId = relationTypeApi.findRelationTypes(builders.get("FindRelationTypesRequest")
                                    .sourceTypeName(acronymRelationSourceType).targetTypeName(acronymRelationTargetType)
                                    .role(acronymRelation).build()).getResults()*.getId()
    loggerApi.info("-----Got acronym id "+acronymRelationId)
    addRelationToAsset(assetId, acronymRelationId, relatedAsset)

    //Find the users(ID's) that are responsible for the target domain with specified role
    execution.setVariable('technicalStewardUser', responsibleUsers(changedDomain, technicalStewardRole))
    execution.setVariable('businessStewardUser', responsibleUsers(changedDomain, businessStewardRole))
    loggerApi.info("------business stewards------- "+businessStewardUser)
    loggerApi.info('------Responsible user fetched successfully------')

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

        def responsibleUserIds = responsibilityApi.findResponsibilities(builders.get('FindResponsibilitiesRequest')
                                    .resourceIds(responsibilityList).roleIds(rolesIdList) 
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


