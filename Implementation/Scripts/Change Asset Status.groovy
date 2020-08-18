/*Change Asset Status script V 1.0
Author: N. Padma Gokul
Description: This script is used to change the 
status of the asset.
*/

//Under Review Status
def underReviewStatusId = statusApi.getStatusByName(underReviewStatus).getId()
loggerApi.info("-----Under Review Id "+underReviewStatusId)

def changedAsset = assetApi.changeAsset(builders.get("ChangeAssetRequest").id(string2Uuid(assetId))
    .statusId(underReviewStatusId).build()
)

//Accepted Status
acceptedStatusId = statusApi.getStatusByName(acceptedStatus).getId()

assetApi.changeAsset(builders.get("ChangeAssetRequest").id(string2Uuid(assetId)).domainId(changedDomain).build())

def changedAsset = assetApi.changeAsset(builders.get("ChangeAssetRequest").id(string2Uuid(assetId))
    .statusId(acceptedStatusId).build()
)

