/*
Script Name: List Columns Script
Author: N. Padma Gokul
Version: v1.0
Version History: -
Purpose: This script is used to list all the columns of the added tables.
*/

def tableList = []
def targetList = []
def columnsList = []

columnRelationId = relationTypeApi.findRelationTypes(builders.get("FindRelationTypesRequest").role(columnRelation).sourceTypeName(columnSourceType)
                                .build()).getResults()*.getId()

//Get all source columns from given table(s)
relatedTables.each{t ->
        tableList.add(relationApi.findRelations(builders.get("FindRelationsRequest")
            .relationTypeId(columnRelationId)
            .targetId(t).build()).getResults()*.getSource())
}

//Get all target tables from given table(s)
relatedTables.each{t ->
        targetList.add(relationApi.findRelations(builders.get("FindRelationsRequest")
            .relationTypeId(columnRelationId)
            .targetId(t).build()).getResults()*.getTarget())
}

//Add columns to list to show to DG Analyst
loggerApi.info("---------Source Columns List----------"+tableList)
loggerApi.info("---------Target Table List----------"+targetList)

for(int table=0; table<tableList.size; table++){
    for(tables in tableList[table]){
        columnsList.add(tables.getId())
    }
}

execution.setVariable("columnList", columnsList)

loggerApi.info("---------Columns List----------"+columnsList)