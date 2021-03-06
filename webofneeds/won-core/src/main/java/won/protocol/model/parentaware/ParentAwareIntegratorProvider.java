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

package won.protocol.model.parentaware;

import org.hibernate.integrator.spi.Integrator;
import org.hibernate.jpa.boot.spi.IntegratorProvider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Integrator provider preconfigured to return the ParentAwareEventListenerIntegrator.
 */
public class ParentAwareIntegratorProvider implements IntegratorProvider
{
  private List integrators = new ArrayList<Integrator>(1);

  public ParentAwareIntegratorProvider(final List integrators) {
    this.integrators = integrators;
  }

  public ParentAwareIntegratorProvider() {
    integrators.add(new ParentAwareEventListenerIntegrator());
  }

  @Override
  public List<Integrator> getIntegrators() {
    return Collections.unmodifiableList(integrators);
  }
}
