/*
 * Copyright 2012  Research Studios Austria Forschungsges.m.b.H.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package won.bot.framework.component.needproducer.impl;

import org.apache.jena.query.Dataset;
import org.apache.jena.query.DatasetFactory;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import won.bot.framework.component.needproducer.FileBasedNeedProducer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * User: fkleedorfer
 * Date: 17.12.13
 */
public class TrigFileNeedProducer implements FileBasedNeedProducer
{
  private final Logger logger = LoggerFactory.getLogger(getClass());

  @Override
  public  synchronized Model readNeedFromFile(final File file) throws IOException
  {
    logger.debug("processing as turtle file: {} ", file);
    try (FileInputStream fis = new FileInputStream(file)) {
      Dataset dataset = DatasetFactory.createGeneral();
      RDFDataMgr.read(dataset, fis, RDFFormat.TRIG.getLang());
      // TODO this needs to be fixed according to the need trig format
      // definition, i.e. the need/connection/event/etc. triples are in core
      // and transient named graphs, and in default graph there can only be
      // the graphs signatures. The Dataset should be returned instead of
      // Model
      return dataset.getDefaultModel();
    } catch (Exception e) {
      logger.debug("could not parse trig from file {} ", file, e);
      throw e;
    }
  }


}
