/*
 * The Alluxio Open Foundation licenses this work under the Apache License, version 2.0
 * (the "License"). You may not use this work except in compliance with the License, which is
 * available at www.apache.org/licenses/LICENSE-2.0
 *
 * This software is distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied, as more fully set forth in the License.
 *
 * See the NOTICE file distributed with this work for information regarding copyright ownership.
 */

import { AxiosResponse } from 'axios';
import { action } from 'typesafe-actions';

import { StacksActionTypes } from './types';
import { AnyAction } from 'redux';

export const fetchRequest = (): AnyAction => action(StacksActionTypes.FETCH_REQUEST);
export const fetchSuccess = (response: AxiosResponse): AnyAction => action(StacksActionTypes.FETCH_SUCCESS, response);
export const fetchError = (message: string): AnyAction => action(StacksActionTypes.FETCH_ERROR, message);
