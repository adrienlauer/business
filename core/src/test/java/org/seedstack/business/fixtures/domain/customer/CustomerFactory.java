/*
 * Copyright © 2013-2017, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.seedstack.business.fixtures.domain.customer;

import org.seedstack.business.domain.Factory;


public interface CustomerFactory extends Factory<Customer> {

  Customer createNewCustomer(String entityId, String firstName, String lastName);

  Customer createNewCustomer(String entityId, String firstName, String lastName, String addressType,
      String line1, String line2, String zipCode, String country);

}

