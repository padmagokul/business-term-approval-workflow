
       import com.collibra.dgc.core.api.dto.instance.asset.AddAssetRequest;
       import com.collibra.dgc.core.api.dto.instance.attribute.AddAttributeRequest;
       import com.collibra.dgc.core.api.dto.instance.relation.AddRelationRequest;

       def note = execution.getVariable("note")
       def definition = execution.getVariable("definition")

       def newAssetUuid = assetApi.addAsset(AddAssetRequest.builder().name(signifier).displayName(signifier).typeId(conceptType)
           .domainId(string2Uuid(intakeVocabulary))
           .build()).getId()
        
       addAttributeToAsset(newAssetUuid,definition,definitionAttributeTypeUuid)
       addAttributeToAsset(newAssetUuid,note,noteAttributeTypeUuid)

       execution.setVariable("outputCreatedTermId",uuid2String(newAssetUuid))


       def addAttributeToAsset(assetUuid,attributeValue,attributeTypeUuid) {
           if (attributeValue == null){
               return;
           }
           attributeApi.addAttribute(AddAttributeRequest.builder()
                    .assetId(assetUuid)
                    .typeId(string2Uuid(attributeTypeUuid))
                    .value(attributeValue.toString())
                    .build())
       }
