/*
 * Copyright 2017 Huawei Technologies Co., Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.servicecomb.provider.springmvc.reference;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.http.HttpMethod;

import io.servicecomb.common.rest.RestConst;
import io.servicecomb.core.Invocation;
import io.servicecomb.core.definition.OperationMeta;
import io.servicecomb.core.invocation.InvocationFactory;
import io.servicecomb.core.provider.consumer.ReferenceConfig;
import io.servicecomb.provider.springmvc.reference.UrlWithServiceNameClientHttpRequestFactory.UrlWithServiceNameClientHttpRequest;
import io.servicecomb.swagger.invocation.Response;
import mockit.Deencapsulation;
import mockit.Expectations;
import mockit.Mocked;

public class TestUrlWithServiceNameClientHttpRequestFactory {
  UrlWithServiceNameClientHttpRequestFactory factory = new UrlWithServiceNameClientHttpRequestFactory();

  URI uri = URI.create("cse://ms/v1/path");

  @Test
  public void findUriPath() throws IOException {
    UrlWithServiceNameClientHttpRequest request =
        (UrlWithServiceNameClientHttpRequest) factory.createRequest(uri, HttpMethod.GET);

    Assert.assertEquals("/ms/v1/path", request.findUriPath(uri));
  }

  @Test
  public void invoke_checkPath(@Mocked Invocation invocation, @Mocked RequestMeta requestMeta) {
    Map<String, String> handlerContext = new HashMap<>();
    UrlWithServiceNameClientHttpRequest request = new UrlWithServiceNameClientHttpRequest(uri, HttpMethod.GET) {
      @Override
      protected Response doInvoke(Invocation invocation) {
        return Response.ok(null);
      }
    };

    new Expectations(InvocationFactory.class) {
      {
        invocation.getHandlerContext();
        result = handlerContext;
        InvocationFactory.forConsumer((ReferenceConfig) any, (OperationMeta) any, (Object[]) any);
        result = invocation;
      }
    };

    Deencapsulation.setField(request, "requestMeta", requestMeta);
    Deencapsulation.setField(request, "path", request.findUriPath(uri));

    Deencapsulation.invoke(request, "invoke", new Object[] {new Object[] {}});

    Assert.assertEquals("/ms/v1/path?null", handlerContext.get(RestConst.REST_CLIENT_REQUEST_PATH));
  }
}
