import com.collibra.dgc.core.api.dto.instance.asset.ChangeAssetRequest

def changedAsset = assetApi.changeAsset(ChangeAssetRequest.builder().id(item.id)
    .statusId(string2Uuid("00000000-0000-0000-0000-000000005009"))
    .build()
)