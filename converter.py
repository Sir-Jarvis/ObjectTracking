import tensorflow as tf

saved_model_dir = '/Users/mac/Documents/DIC2/ComputerVision/App/Object_Detection/Tensorflow/workspace/training/models/ssd_mobilenet_v2_fpnlite/saved_model'
# Convert the model
converter = tf.lite.TFLiteConverter.from_saved_model(saved_model_dir) # path to the SavedModel directory
tflite_model = converter.convert()

# Save the model.
with open('model.tflite', 'wb') as f:
  f.write(tflite_model)