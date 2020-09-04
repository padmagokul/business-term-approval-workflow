/*
Script Name: List Columns Script
Author: N. Padma Gokul
Version: v1.0
Version History: -
Purpose: This script is used to add the column relation to the asset.
*/

    representsRelationId = relationTypeApi.findRelationTypes(builders.get("FindRelationTypesRequest").role(representsRelation).sourceTypeName("Business Term")
                                    .build()).getResults()*.getId()

    loggerApi.info("---------Selected columns-------"+addColumns)

    columnRelationId = relationTypeApi.findRelationTypes(builders.get("FindRelationTypesRequest").role(columnRelation).sourceTypeName(columnSourceType)
                              .build()).getResults()*.getId()

    def addRelation = []
    //Add "represents" relation to asset
    addColumns.each{t ->
        addRelation.add(builders.get("AddRelationRequest").sourceId(string2Uuid(assetId)).targetId(t)
            .typeId(representsRelationId[0]).build())
    }

      relationApi.addRelations(addRelation)
      loggerApi.info("-----Column Relation added--------")

      //Get the added column names to display to technical steward
      def dgcUrl = "http://192.168.168.22:4500/asset/"
      def columnNames = []
      def tableList = []
      def tableId = []
      def tableName = []
      def columnTableLink = []
      def columnUrl = []

      //Get the table details for added columns
      addColumns.each{ t ->
        tableList.add(relationApi.findRelations(builders.get("FindRelationsRequest")
            .relationTypeId(columnRelationId)
            .sourceId(t).build()).getResults()*.getTarget())
    }

      //Get the table Id from table details
      for(int tableNo=0; tableNo<tableList.size; tableNo++){
          for(tables in tableList[tableNo]){
              tableId.add(tables.getId())
         }
      }

      loggerApi.info("-----Table Id for each column------"+tableId)

      //Get the column name
      addColumns.each{ t ->
      columnNames.add(assetApi.getAsset(t).getName())
      }

      addColumns.each{ t ->
      columnUrl.add(dgcUrl+t)
      }

      loggerApi.info("------Column URL-----"+columnUrl)

      //Get the table name (Need to test this..)
      tableId.each{ t ->
      tableName.add(assetApi.getAsset(t).getName()) 
      }

      loggerApi.info("-------Table Names--------"+tableName)

      //Script to display the Column name and it's source table name
      def table = 0
      def url = 0
      for(def column = 0; column<columnNames.size; column++){
        def tableHyperlink = "<a href="+columnUrl[url]+" target=_blank>"
        loggerApi.info("--Link--"+tableHyperlink)
        def closeTag = "</a>"
        columnTableLink.add("Column Name: "+tableHyperlink+columnNames[column]+closeTag+"; Table Name: "+tableName[table])
        table++
        url++
      }

      loggerApi.info("------Column Names------"+columnTableLink)
      execution.setVariable("columnNames", columnTableLink.join(', '))

