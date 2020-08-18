/*Add Column relation script V 1.0
Author: N. Padma Gokul
Description: This script is used to add the column
relation to the table. 
*/

    representsRelationId = relationTypeApi.findRelationTypes(builders.get("FindRelationTypesRequest").role(representsRelation).sourceTypeName("Business Term")
                                    .build()).getResults()*.getId()

    def addRelation = []
    //Add "represents" relation to asset
    addColumns.each{t ->
        addRelation.add(builders.get("AddRelationRequest").sourceId(string2Uuid(assetId)).targetId(t)
            .typeId(representsRelationId[0]).build())
    }

      relationApi.addRelations(addRelation)
      loggerApi.info("-----Column Relation added--------")

        def columnNames=[]
        addColumns.each{ t ->
        columnNames.add(assetApi.getAsset(t).getName())
        }
        execution.setVariable("columnNames", columnNames)