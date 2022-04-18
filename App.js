import { StatusBar } from 'expo-status-bar';
import * as React from 'react';
import { StyleSheet, Text, View, Button, Alert } from 'react-native';
import { InferenceSession, Tensor, TensorConstructor } from 'onnxruntime-react-native';
import DataHandler from './dataHandler';
import react from 'react';

function getMaxIndex(array) {
  var max = array[0];
  var maxIndex = 0;
  for (var i = 1; i < array.length; i++) {
    if (array[i] > max) {
      maxIndex = i;
      max = array[i];
    }
  }
  return maxIndex;
}

async function runModel(setMessage) {
  try {
    const path =  await DataHandler.getLocalModelPath();
    const session = await InferenceSession.create(path);
    const empty_array = new Float32Array(224*224*3)
    const fake_data = empty_array.map(() => Math.random());
    const input_tensor = new Tensor(fake_data, [1, 3, 224, 224]);
    const feed = {
      input: input_tensor
    }
    const output = await session.run(feed, session.outputNames);
    const max = getMaxIndex(output[session.outputNames[0]].data);
    const max_val = output[session.outputNames[0]].data[max];
    setMessage(`The predicted class is Class # ${max} with a magnitude of ${max_val}`);
  }
  catch (e) {
    setMessage(`error: ${e}`);
  }
}

export default function App() {
  const [message, setMessage] = react.useState('');
  
  return (
    <View style={styles.container}>
      <Text>{message}</Text>
      <Text>{require('./assets/mobilenet.ort')}</Text>
      <Button title="Start Inference" onPress={()=>{runModel(setMessage)} }/>
      <StatusBar style="auto" />
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#fff',
    alignItems: 'center',
    justifyContent: 'center',
  },
});
