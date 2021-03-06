package won.protocol.util;

import org.apache.jena.rdf.model.*;
import org.apache.jena.vocabulary.DCTerms;
import org.apache.jena.vocabulary.RDF;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import won.protocol.model.Need;
import won.protocol.model.NeedState;
import won.protocol.vocabulary.WON;

import java.net.URI;

/**
 * User: gabriel
 * Date: 09.04.13
 * Time: 15:36
 */
public class NeedModelMapper implements ModelMapper<Need>
{
  final Logger logger = LoggerFactory.getLogger(getClass());

  @Override
  public Model toModel(Need need)
  {
    Model model = ModelFactory.createDefaultModel();
    Resource needResource = model.createResource(need.getNeedURI().toString(), WON.NEED);
    Literal creationDate = DateTimeUtils.toLiteral(need.getCreationDate(), model);
    if (creationDate != null) {
      model.add(model.createStatement(needResource, DCTerms.created, creationDate));
    }
    model.add(model.createStatement(needResource, WON.IS_IN_STATE, WON.toResource(need.getState())));

    // We don't add the need owner's endpoint here as this is confidential information

    return model;
  }

  @Override
  public Need fromModel(Model model)
  {
    Need need = new Need();

    ResIterator needIt = model.listSubjectsWithProperty(RDF.type, WON.NEED);
    if (!needIt.hasNext()) throw new IllegalArgumentException("at least one RDF node must be of type won:Need");

    Resource needRes = needIt.next();
    logger.debug("processing need resource {}", needRes.getURI());

    need.setNeedURI(URI.create(needRes.getURI()));

    Statement dateStat = needRes.getProperty(DCTerms.created);
    if (dateStat != null && dateStat.getObject().isLiteral()) {
      need.setCreationDate(DateTimeUtils.toDate(dateStat.getObject().asLiteral(), model));
      logger.debug("found needCreationDate literal value '{}'",dateStat.getObject().asLiteral().getString());
    } else {
      logger.debug("no needCreationDate property found for need resource {}", needRes.getURI());
    }

    Statement stateStat = needRes.getProperty(WON.IS_IN_STATE);
    if (stateStat != null && stateStat.getObject().isResource()) {
      URI uri = URI.create(stateStat.getResource().getURI());
      need.setState(NeedState.parseString(uri.getFragment()));
      logger.debug("found isInState literal value '{}'",stateStat.getObject().asResource().getURI());
    }  else {
      logger.debug("no isInState property found for need resource {}", needRes.getURI());
    }
    need.setWonNodeURI(URI.create(needRes.getPropertyResourceValue(WON.HAS_WON_NODE).toString()));

    return need;
  }
}
