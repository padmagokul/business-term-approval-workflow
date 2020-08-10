import com.collibra.dgc.core.api.dto.instance.responsibility.FindResponsibilitiesRequest
import com.collibra.dgc.core.api.dto.role.FindRolesRequest

def responsibilityList = []

def rolesList = roleApi.findRoles(FindRolesRequest.builder().name("Business Steward") 
    .build()).getResults()*.getId()

loggerApi.info("Role ID found "+rolesList)

responsibilityList.add(string2Uuid("c306fc42-dd58-4ccf-aa03-0af16d79919f"))

def resultList = responsibilityApi.findResponsibilities(FindResponsibilitiesRequest.builder()
    .resourceIds(responsibilityList).roleIds(rolesList) 
    .build()).getResults()*.getOwner()*.getId()

loggerApi.info("User IDs for the responsibility on domain "+resultList)

