
domainTypeID = domainTypeApi.findDomainTypes(builders.get("FindDomainTypesRequest").name(domainType).build()).getResults()*.getId()
loggerApi.info("Glossary Domain Id: "+domainTypeID)
execution.setVariable("domainTypeId", domainTypeID[0])


${utility.toIdList(domainApi.findDomains(builders.get("FindDomainsRequest").typeId(domainTypeId).build()).getResults())}