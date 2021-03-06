/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2017 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms
 * of the GNU Affero General Public License as published by the Free Software Foundation, either
 * version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * See the GNU Affero General Public License for more details. You should have received a copy of
 * the GNU Affero General Public License along with this program. If not, see
 * http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.stockmanagement.web;

import org.openlmis.stockmanagement.dto.StockEventDto;
import org.openlmis.stockmanagement.service.StockEventProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.UUID;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * Controller used to create stock event.
 */
@Controller
@RequestMapping("/api")
public class StockEventsController {

  @Autowired
  private StockEventProcessor stockEventProcessor;

  @RequestMapping(value = "stockEvents", method = POST)
  public ResponseEntity<UUID> createStockEvent(@RequestBody StockEventDto stockEventDto)
          throws InstantiationException, IllegalAccessException {
    UUID createdEventId = stockEventProcessor.process(stockEventDto);
    return new ResponseEntity<>(createdEventId, CREATED);
  }

}
