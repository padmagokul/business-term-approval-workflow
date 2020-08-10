import com.collibra.dgc.core.api.dto.instance.asset.ChangeAssetRequest

def changedDomain = assetApi.changeAsset(ChangeAssetRequest.builder().id(item.id).domainId(string2Uuid("3574eccd-07ab-47ae-a657-163fc36020f2")).build())