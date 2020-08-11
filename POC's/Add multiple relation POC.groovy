import com.collibra.dgc.core.api.dto.instance.relation.AddRelationRequest;


loggerApi.info("---------Start of Workflow----------")
def relatedAsset = execution.getVariable("relatedAsset")

loggerApi.info("----Addng Acoronyms to BT-------")

addAcronymRelationToAsset(assetId, acronymRelationId, relatedAsset)

def addAcronymRelationToAsset(assetId, acronymRelationId, relatedAsset) {
  if (relatedAsset == null || relatedAsset.isEmpty()){
      return
  }
  def addRelationRequests = []

  relatedAsset.each{ t ->

   loggerApi.info("Adding Acronym ID => ${t}")
   addRelationRequests.add(AddRelationRequest.builder()
       .sourceId(assetId)
       .targetId(t)
       .typeId(string2Uuid(acronymRelationId))
       .build())
}


  def relationList = []
  relationList = relationApi.addRelations(addRelationRequests)

  loggerApi.info("----------Relation Added successfully--------------------")

}
loggerApi.info("---------End of Workflow----------")