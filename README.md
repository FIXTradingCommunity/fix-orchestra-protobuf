# fix-orchestra-protobuf
Translators from FIX Orchestra to the protobuf family of serializations.

----
## Supported Serializations

* Protocol Buffers ver 2
* Protocol Buffers ver 3
* Cap'n Proto

Generation of schema files is based on [Encoding FIX using GPB Release Candidate 3](https://www.fixtrading.org/standards/gpb/) specifications. Note that generation of .capnp files is also based on this spec and is done so without much consideration of the differences between the two encodings. I.e. there may be other ways to generate .capnp files to better leverage Cap'n Proto's efficiencies. 

----
## Prerequisites

* Java bindings for the orchestra XML schema are provided by the [repository2016 module](https://github.com/FIXTradingCommunity/fix-orchestra/tree/master/repository2016).

## Build

The project is built with Maven version 3.0 or later.

----
## Usage

java -jar orchestra2proto-jar-with-dependencies.jar [options] *orchestra_file*

    Options:
    -altfs         Use alternate output file packaging
    -help          Print this message
    -lang <arg>    Schema language (valid args: proto2 proto3 capnp)
    -nosort        Maintain repo field ordering
    -opath <arg>   Output directory
    -version       Print version

----
## License

© *2019 FIX Protocol Limited*

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

