/**
 * Copyright (c) 2013-2015 by The SeedStack authors. All rights reserved.
 *
 * This file is part of SeedStack, An enterprise-oriented full development stack.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.business.api.domain.annotations.stereotypes;

import java.lang.annotation.*;

/**
 * This annotation indicates a creation. Be careful, a creation is related to object instantiation not to persistence.
 * For instance, it could be used on a factory.
 *
 * @author pierre.thirouin@ext.mpsa.com
 *         Date: 05/06/2014
 */
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.ANNOTATION_TYPE, ElementType.TYPE, ElementType.METHOD})
public @interface Create {
}