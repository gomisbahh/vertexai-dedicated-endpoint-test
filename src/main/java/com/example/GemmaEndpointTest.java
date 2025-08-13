package com.example;


import com.google.cloud.aiplatform.v1.EndpointName;
import com.google.cloud.aiplatform.v1.PredictionServiceClient;
import com.google.cloud.aiplatform.v1.PredictionServiceSettings;
import com.google.protobuf.Value;
import com.google.protobuf.util.JsonFormat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GemmaEndpointTest {

  public static void main(String[] args) throws IOException {
    // TODO(developer): Replace these variables before running the sample.

    String projectId = "106917695099";
    String location = "us-east1";
    String endpointId = "7871795169387872256";
    String prompt = "what is the color of the sky";

    callModelGarden(projectId, location, endpointId, prompt);
  }

  public static void callModelGarden(
          String projectId, String location, String endpointId, String prompt)
          throws IOException {

    String endpoint = String.format("%s.%s-%s.prediction.vertexai.goog:443", endpointId, location, projectId);

    String endpointString = "projects/106917695099/locations/us-east1/endpoints/7871795169387872256";

    PredictionServiceSettings settings =
            PredictionServiceSettings.newBuilder().setEndpoint(endpoint).build();

    try (PredictionServiceClient client = PredictionServiceClient.create(settings)) {

      EndpointName endpointName = EndpointName.of(projectId,location,endpointId);

      String instanceJson = String.format("{ \"prompt\": \"%s\" }", prompt);
      Value.Builder instanceBuilder = Value.newBuilder();
      JsonFormat.parser().merge(instanceJson, instanceBuilder);

      List<Value> instances = new ArrayList<>();
      instances.add(instanceBuilder.build());

      // Construct the parameters payload to control the endpointId's output
      String parametersJson =
              "{ \"temperature\": 0.2, \"maxOutputTokens\": 150, \"topP\": 0.8, \"topK\": 40 }";
      Value.Builder parametersBuilder = Value.newBuilder();
      JsonFormat.parser().merge(parametersJson, parametersBuilder);
      Value parameters = parametersBuilder.build();

      // Send the prediction request
      System.out.println("Sending request to Vertex AI...");
      var response = client.predict(endpointName, instances, parameters);
      //var response = client.predict(endpointString, instances, parameters);

      System.out.println("Response received.");

      Value prediction = response.getPredictions(0);
      String answer = prediction.getStructValue().getFieldsMap().get("content").getStringValue();

      System.out.println("\n--- Prompt ---");
      System.out.println(prompt);
      System.out.println("\n---  Answer ---");
      System.out.println(answer.trim());

    } catch (Exception e) {
      System.err.println("Error during prediction: " + e.getMessage());
      e.printStackTrace();
    }
  }
}