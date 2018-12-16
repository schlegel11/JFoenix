/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.jfoenix.transitions.template;

import com.jfoenix.transitions.template.helper.FromToKeyValueCreator;
import com.jfoenix.transitions.template.helper.InterpolatorFactory;
import com.jfoenix.transitions.template.helper.KeyValueWrapper;
import com.jfoenix.transitions.template.helper.TargetResetHelper;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.value.WritableValue;
import javafx.event.ActionEvent;
import javafx.util.Duration;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Class which represents the specific animation implementations.
 *
 * @author Marcel Schlegel (schlegel11)
 * @version 1.0
 * @since 2018-09-22
 */
public class JFXAnimationTemplates {

  private JFXAnimationTemplates() {}

  /**
   * The {@link Timeline} implementation which supports all {@link JFXAnimationTemplateAction} and
   * {@link JFXAnimationTemplateConfig} methods.
   */
  public static <N> Timeline buildTimeline(JFXAnimationTemplate<N> creator) {

    Timeline timeline = new Timeline();
    JFXAnimationTemplateConfig templateConfig = creator.buildAndGetConfig();

    AtomicReference<Duration> maxDuration = new AtomicReference<>(templateConfig.getDuration());
    Map<Duration, List<JFXAnimationTemplateAction<?, ?>>> actionMap =
        creator.buildAndGetActions(
            key -> {
              Duration duration = calcActionDuration(key, templateConfig);
              // Get the maximal duration during key mapping.
              if (duration.greaterThan(maxDuration.get())) {
                maxDuration.set(duration);
              }
              return duration;
            });

    FromToKeyValueCreator<KeyValueWrapper<KeyValue>> fromToKeyValueCreator =
        new FromToKeyValueCreator<>(Duration.ZERO, maxDuration.get());
    TargetResetHelper<KeyValueWrapper<KeyValue>> targetResetHelper = new TargetResetHelper<>();

    actionMap.forEach(
        (duration, actions) -> {

          // Create the key values.
          KeyValue[] keyValues =
              actions
                  .stream()
                  .flatMap(action -> action.mapTo(createKeyValueFunction(templateConfig, action)))
                  .toArray(KeyValue[]::new);

          // Reduce the onFinish events to one consumer.
          Consumer<ActionEvent> onFinish =
              actions
                  .stream()
                  .map(
                      action ->
                          (Consumer<ActionEvent>)
                              actionEvent -> {
                                if (action.isExecuted()) {
                                  action.handleOnFinish(actionEvent);
                                  action.addExecution(1);
                                }
                                action.handleOnFinishInternal();
                              })
                  .reduce(action -> {}, Consumer::andThen);

          KeyFrame keyFrame = new KeyFrame(duration, onFinish::accept, keyValues);
          timeline.getKeyFrames().add(keyFrame);

          if (templateConfig.isFromToAutoGen() || templateConfig.isAutoReset()) {
            List<KeyValueWrapper<KeyValue>> keyValueWrappers =
                actions
                    .stream()
                    .flatMap(
                        action ->
                            action.mapTo(
                                writableValue ->
                                    new KeyValueWrapper<>(
                                        new KeyValue(
                                            writableValue,
                                            writableValue.getValue(),
                                            InterpolatorFactory
                                                .createFromToAutoKeyFrameInterpolator(
                                                    writableValue, templateConfig, action)),
                                        writableValue)))
                    .collect(Collectors.toList());

            if (templateConfig.isFromToAutoGen()) {
              keyValueWrappers.forEach(
                  keyValueWrapper ->
                      fromToKeyValueCreator.computeKeyValue(duration, keyValueWrapper));
            }

            if (templateConfig.isAutoReset()) {
              targetResetHelper.computeKeyValues(
                  keyValueWrappers,
                  keyValueWrapper -> {
                    KeyValue keyValue = keyValueWrapper.getKeyValue();
                    keyValueWrapper.getWritableValue().setValue(keyValue.getEndValue());
                  });
            }
          }
        });

    fromToKeyValueCreator
        .getStartKeyValues()
        .map(values -> values.stream().map(KeyValueWrapper::getKeyValue).toArray(KeyValue[]::new))
        .ifPresent(
            keyValues -> {
              KeyFrame keyFrame = new KeyFrame(fromToKeyValueCreator.getStartDuration(), keyValues);
              timeline.getKeyFrames().add(keyFrame);
            });

    fromToKeyValueCreator
        .getEndKeyValues()
        .map(values -> values.stream().map(KeyValueWrapper::getKeyValue).toArray(KeyValue[]::new))
        .ifPresent(
            keyValues -> {
              KeyFrame keyFrame = new KeyFrame(fromToKeyValueCreator.getEndDuration(), keyValues);
              timeline.getKeyFrames().add(keyFrame);
            });

    timeline.setAutoReverse(templateConfig.isAutoReverse());
    timeline.setCycleCount(templateConfig.getCycleCount());
    timeline.setDelay(templateConfig.getDelay());
    timeline.setRate(templateConfig.getRate());
    timeline.setOnFinished(
        event -> {
          templateConfig.handleOnFinish(event);
          targetResetHelper.reset();
        });

    return timeline;
  }

  private static Function<WritableValue<Object>, KeyValue> createKeyValueFunction(
      JFXAnimationTemplateConfig config, JFXAnimationTemplateAction<?, ?> action) {
    return (writableValue) ->
        new KeyValue(
            writableValue,
            action.getEndValue(),
            InterpolatorFactory.createKeyFrameInterpolator(writableValue, config, action));
  }

  private static Duration calcActionDuration(
      JFXAnimationTemplate.ActionKey key, JFXAnimationTemplateConfig config) {
    return key.getPercentOptional()
        // calc the percentage duration of total duration.
        .map(percent -> config.getDuration().multiply((percent / 100)))
        .orElse(key.getTime());
  }
}
