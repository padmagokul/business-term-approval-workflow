def tempList=[]
def newlist=[]
relatedTables.each{t ->
tempList.add(relationApi.findRelations(builders.get("FindRelationsRequest")
.relationTypeId(string2Uuid("00000000-0000-0000-0000-000000007042"))
.targetId(t).build()).getResults()*.getSource())
}

loggerApi.info("---------First Columns List----------"+tempList)
for(i=0;i<tempList.size;i++){
    for(item in tempList[i]){
        newlist.add(item.getId())
    }
    loggerApi.info("---List "+i+" added to newlist---")
}
execution.setVariable("columnList",newlist)
loggerApi.info("---Column List variable created------")

loggerApi.info("---------New Columns List----------"+newlist)