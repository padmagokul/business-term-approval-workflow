import com.collibra.dgc.core.api.dto.instance.relation.AddRelationRequest;


loggerApi.info("---------Start of Workflow----------")
def relatedAsset = execution.getVariable("relatedAsset")

loggerApi.info("----Addng Acoronyms to BT ${item.name}-------")

usesRelationId = relationTypeApi.findRelationTypes(builders.get("FindRelationTypesRequest").role(usesRelationId).build()).getResults()*.getId()
loggerApi.info("-------Got Acronym Relation Id------------")

addRelationsWithOneSourceAndMultipleTargetsToAsset(item.id,usesRelationId,relatedAsset)

def addRelationsWithOneSourceAndMultipleTargetsToAsset(sourceUuid,relationTypeUuid,targetUuidList) {
  if (targetUuidList == null || targetUuidList.isEmpty()){
      return
  }
  def addRelationRequests = []

  targetUuidList.each{ t ->

   loggerApi.info("Adding Acronym ID => ${t}")
      addRelationRequests.add(AddRelationRequest.builder()
          .sourceId(sourceUuid).targetId(t)
          .typeId(string2Uuid(relationTypeUuid))
          .build())
}

  def relationList = []
  relationList = relationApi.addRelations(addRelationRequests)

  loggerApi.info("----------Added Relations to BT ${item.id} =>>> ${relationList}--------------------")

}
loggerApi.info("---------End of Workflow----------")