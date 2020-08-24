/*
Script Name: List Columns Script
Author: N. Padma Gokul
Version: v1.0
Version History: -
Purpose: This script is used to add the column relation to the asset.
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