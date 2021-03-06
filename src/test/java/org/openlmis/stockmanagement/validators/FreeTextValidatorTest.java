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

package org.openlmis.stockmanagement.validators;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.stockmanagement.domain.adjustment.StockCardLineItemReason;
import org.openlmis.stockmanagement.domain.movement.Node;
import org.openlmis.stockmanagement.dto.StockEventDto;
import org.openlmis.stockmanagement.exception.ValidationMessageException;
import org.openlmis.stockmanagement.repository.NodeRepository;
import org.openlmis.stockmanagement.repository.StockCardLineItemReasonRepository;
import org.openlmis.stockmanagement.testutils.StockEventDtoBuilder;

import java.util.UUID;

import static java.util.UUID.fromString;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.openlmis.stockmanagement.i18n.MessageKeys.ERROR_DESTINATION_FREE_TEXT_NOT_ALLOWED;
import static org.openlmis.stockmanagement.i18n.MessageKeys.ERROR_REASON_FREE_TEXT_NOT_ALLOWED;
import static org.openlmis.stockmanagement.i18n.MessageKeys.ERROR_SOURCE_DESTINATION_FREE_TEXT_BOTH_PRESENT;
import static org.openlmis.stockmanagement.i18n.MessageKeys.ERROR_SOURCE_FREE_TEXT_NOT_ALLOWED;

@RunWith(MockitoJUnitRunner.class)
public class FreeTextValidatorTest {

  @InjectMocks
  FreeTextValidator freeTextValidator;

  @Mock
  NodeRepository nodeRepository;

  @Mock
  StockCardLineItemReasonRepository stockCardLineItemReasonRepository;

  @Before
  public void setUp() throws Exception {
    Node mockNode = mock(Node.class);
    when(nodeRepository.findOne(any(UUID.class))).thenReturn(mockNode);
    when(mockNode.isRefDataFacility()).thenReturn(true);
  }

  @Test
  public void should_fail_when_event_has_both_source_and_destination_free_text() throws Exception {
    StockEventDto eventDto = StockEventDtoBuilder.createStockEventDto();
    eventDto.setReasonId(null);
    eventDto.setSourceFreeText("source free text");
    eventDto.setDestinationFreeText("destination free text");

    try {
      freeTextValidator.validate(eventDto);
    } catch (ValidationMessageException ex) {
      assertThat(ex.asMessage().toString(),
          containsString(ERROR_SOURCE_DESTINATION_FREE_TEXT_BOTH_PRESENT));
      return;
    }

    fail();
  }

  @Test
  public void should_fail_when_source_node_is_refdata_with_free_text() throws Exception {
    StockEventDto eventDto = StockEventDtoBuilder.createStockEventDto();
    eventDto.setReasonId(null);
    eventDto.setSourceFreeText("source free text");
    eventDto.setDestinationFreeText(null);

    eventDto.setSourceId(fromString("0bd28568-43f1-4836-934d-ec5fb11398e8"));

    try {
      freeTextValidator.validate(eventDto);
    } catch (ValidationMessageException ex) {
      assertThat(ex.asMessage().toString(), containsString(ERROR_SOURCE_FREE_TEXT_NOT_ALLOWED));
      return;
    }

    fail();
  }

  @Test
  public void should_fail_when_destination_node_is_refdata_with_free_text() throws Exception {
    StockEventDto eventDto = StockEventDtoBuilder.createStockEventDto();
    eventDto.setReasonId(null);
    eventDto.setSourceId(null);
    eventDto.setSourceFreeText(null);
    eventDto.setDestinationFreeText("destination free text");

    eventDto.setDestinationId(fromString("0bd28568-43f1-4836-934d-ec5fb11398e8"));

    try {
      freeTextValidator.validate(eventDto);
    } catch (ValidationMessageException ex) {
      assertThat(ex.asMessage().toString(),
          containsString(ERROR_DESTINATION_FREE_TEXT_NOT_ALLOWED));
      return;
    }

    fail();
  }

  @Test
  public void should_fail_when_reason_free_text_not_allowed_but_exist() throws Exception {
    StockCardLineItemReason mockReason = mock(StockCardLineItemReason.class);
    when(stockCardLineItemReasonRepository.findOne(any(UUID.class))).thenReturn(mockReason);
    when(mockReason.getIsFreeTextAllowed()).thenReturn(false);

    StockEventDto eventDto = StockEventDtoBuilder.createStockEventDto();
    eventDto.setSourceFreeText(null);
    eventDto.setDestinationFreeText(null);
    eventDto.setReasonFreeText("reason free text");

    eventDto.setReasonId(fromString("e3fc3cf3-da18-44b0-a220-77c985202e06"));

    try {
      freeTextValidator.validate(eventDto);
    } catch (ValidationMessageException ex) {
      assertThat(ex.asMessage().toString(), containsString(ERROR_REASON_FREE_TEXT_NOT_ALLOWED));
      return;
    }

    fail();
  }
}