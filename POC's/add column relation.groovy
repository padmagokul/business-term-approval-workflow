import com.collibra.dgc.core.api.dto.instance.relation.AddRelationRequest;

def addRelationRequests = []

          columns.each{ t ->
              addRelationRequests.add(AddRelationRequest.builder()
                  .sourceId(item.id)
                  .targetId(t)
                  .typeId(string2Uuid("00000000-0000-0000-0000-000000007038"))
                  .build())
      }

      relationApi.addRelations(addRelationRequests)