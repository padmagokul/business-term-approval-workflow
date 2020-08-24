/*
Script Name: List Columns Script
Author: N. Padma Gokul
Version: v1.0
Version History: -
Purpose: This script is used to list all the columns of the added tables.
*/

def tableList=[]
def columnsList=[]

columnRelationId = relationTypeApi.findRelationTypes(builders.get("FindRelationTypesRequest").role(columnRelation).sourceTypeName(columnSourceType)
                                .build()).getResults()*.getId()

//Get all columns from given table(s)
relatedTables.each{t ->
        tableList.add(relationApi.findRelations(builders.get("FindRelationsRequest")
            .relationTypeId(columnRelationId)
            .targetId(t).build()).getResults()*.getSource())
}

//Add columns to list to show to DG Analyst
loggerApi.info("---------First Columns List----------"+tableList)
for(i=0;i<tableList.size;i++){
    for(tables in tableList[i]){
        columnsList.add(tables.getId())
    }
}

execution.setVariable("columnList", columnsList)

loggerApi.info("---------New Columns List----------"+columnsList)