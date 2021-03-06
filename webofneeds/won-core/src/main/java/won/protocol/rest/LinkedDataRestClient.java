/*
 * Copyright 2012  Research Studios Austria Forschungsges.m.b.H.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package won.protocol.rest;

import org.apache.jena.query.Dataset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

public abstract class LinkedDataRestClient
{

  private final Logger logger = LoggerFactory.getLogger(getClass());


  /**
   * Retrieves RDF for the specified resource URI.
   * Expects that the resource URI will lead to a 303 response, redirecting to the URI where RDF can be downloaded.
   * Paging is not supported.
   *
   * @param resourceURI
   * @return
   */
  public Dataset readResourceData(URI resourceURI) {
    return readResourceDataWithHeaders(resourceURI).getDataset();
  }

  public abstract DatasetResponseWithStatusCodeAndHeaders readResourceDataWithHeaders(URI resourceURI);

  public abstract DatasetResponseWithStatusCodeAndHeaders readResourceDataWithHeaders(URI resourceURI, Map<String, String>
    requestHeaders);

  /**
   * Retrieves RDF for the specified resource URI for the entity with provided WebID.
   * Expects that the resource URI will lead to a 303 response, redirecting to the URI where RDF can be downloaded.
   * Paging is not supported.
   *
   * @param resourceURI
   * @param requesterWebID
   * @return
   */
  public Dataset readResourceData(URI resourceURI, URI requesterWebID){
    return readResourceDataWithHeaders(resourceURI, requesterWebID).getDataset();
  }

  public abstract DatasetResponseWithStatusCodeAndHeaders readResourceDataWithHeaders(URI resourceURI, URI requesterWebID);


  /**
   * Retrieves RDF for the specified resource URI.
   * Expects that the resource URI will lead to a 303 response, redirecting to the URI where RDF can be downloaded.

   *
   * @param resourceURI
   * @return
   */
  public Dataset readResourceData(URI resourceURI, URI requesterWebID, Map<String, String>
    requestHeaders){
    return readResourceDataWithHeaders(resourceURI, requesterWebID, requestHeaders).getDataset();
  }
  public abstract DatasetResponseWithStatusCodeAndHeaders readResourceDataWithHeaders(URI resourceURI, URI requesterWebID,
                                                                                      Map<String,
    String>
    requestHeaders);

  protected DatasetResponseWithStatusCodeAndHeaders readResourceData(URI resourceURI, RestTemplate restTemplate, Map<String,
    String> requestHeaders) {
    assert resourceURI != null : "resource URI must not be null";
    logger.debug("fetching linked data resource: {}", resourceURI);

    //If a RestClientException is thrown here complaining that it can't read a Model with MIME media type text/html,
    //it was probably the wrong resourceURI
    Dataset result;
    int statusCode = -1;
    Map<String, String> responseHeaderMap = null;
    try {
      HttpEntity entity = new HttpEntity(requestHeaders);
      ResponseEntity<Dataset> response = restTemplate.exchange(resourceURI, HttpMethod.GET, entity, Dataset.class);
      //RestTemplate will automatically follow redirects on HttpGet calls
      statusCode = response.getStatusCode().value();
      HttpHeaders responseHeaders = response.getHeaders();
      if (responseHeaders == null){
        responseHeaderMap = new HashMap<>();
      } else {
        responseHeaderMap = responseHeaders.toSingleValueMap();
      }
      if(!response.getStatusCode().is2xxSuccessful()){
        throw new HttpClientErrorException(response.getStatusCode());
      }
      result = response.getBody();
    } catch (RestClientException e) {
      if(e instanceof HttpClientErrorException){
        throw e;
      }
      throw new IllegalArgumentException(
        MessageFormat.format(
          "caught a clientHandler exception, " +
            "which may indicate that the URI that was accessed isn''t a" +
            " linked data URI, please check {0}. Underlying error message is: {1}", resourceURI, e.getMessage()), e);
    }
    if (logger.isDebugEnabled()) {
      logger.debug("fetched model with {} statements in default model for resource {}",result.getDefaultModel().size(),
                   resourceURI);
    }
    return new DatasetResponseWithStatusCodeAndHeaders(result, statusCode, responseHeaderMap);
  }


}
