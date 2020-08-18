/*List Columns script V 1.0
Author: N. Padma Gokul
Description: This script is used to list the columns of
the tables that are added in "Add required tables" task. 
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