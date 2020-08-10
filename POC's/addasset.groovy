import com.collibra.dgc.core.api.dto.instance.asset.AddAssetRequest
import com.collibra.dgc.core.api.dto.instance.attribute.AddAttributeRequest

    def note = execution.getVariable("note")
    def definition = execution.getVariable("definition")     

    loggerApi.info('------Workflow Started--------')

    def newAssetUuid = assetApi.addAsset(AddAssetRequest.builder().name(signifier).displayName(signifier)
        .typeId(string2Uuid(conceptType))
        .domainId(string2Uuid(intakeVocabulary))
        .build()).getId()

    loggerApi.info('------Asset Created Successfully--------')

    addAttributeToAsset(newAssetUuid, definition, definitionAttributeTypeUuid)
    addAttributeToAsset(newAssetUuid, note, noteAttributeTypeUuid)

    loggerApi.info('------Attributes Added Successfully--------')

    execution.setVariable('outputCreatedTermId', uuid2String(newAssetUuid))


    def addAttributeToAsset(assetUuid, attributeValue, attributeTypeUuid) {
        if (attributeValue == null){
            return
        }
        attributeApi.addAttribute(AddAttributeRequest.builder()
                .assetId(assetUuid)
                .typeId(string2Uuid(attributeTypeUuid))
                .value(attributeValue.toString())
                .build())
    }
