# fix-orchestra-protobuf
Translators from FIX Orchestra to the protobuf family of serializations.

----
## Supported Serializations

* Protocol Buffers ver 2
* Protocol Buffers ver 3
* Cap'n Proto

Generation of schema files is based on [Encoding FIX using GPB Release Candidate 3](https://www.fixtrading.org/standards/gpb/) specifications. Note that generation of .capnp files is also based on this spec and is done so without much consideration of the differences between the two encodings. I.e. there may be other ways to generate .capnp files to better leverage Cap'n Proto's efficiencies. 

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

