#!/bin/bash
# run tests for SHACL compliance project

# this is the root folder for output data, set it to any folder you wish (that the docker container can access, if using docker)
# by default is set to the current folder.
BASEDIR=$(dirname $(realpath "$0"))
echo "$BASEDIR"

shared_volume="$BASEDIR"

# whether to use docker for execution
use_docker=false

# whether to build or download built image from dockerhub
download_image=false

# the mode of running tests - inference on IMDB tests requests or evaluation tests with performance results
mode="eval" # {inf (=0), eval (=1), confl (=2)}


# if using inference mode
sparql=false # or true
ultimate=true # or true

# if using evaluation mode
evalMode="imdb_simple" # {imdb_simple, imdb_atomic, random_atomic}

# the seeds to test (for reproducibility)
# use more seeds for imdb_atomic or imdb_simple (for this even more can be added for better results)
# use less seeds (first three, for example) for random_atomic - it takes a lot of memory generating policies and requests
seeds=1,5,10,100,200,500,1000

# number of simple rules per policy to test for each seed
# the bigger the number the worse the memory load, use with caution
policySizes=1,10,100,1000,5000

# number of rules to test
# set to 1 number for imdb_simple and random_simple
# set to array of values for imdb_atomic
if [ "${evalMode}" = "imdb_atomic" ]; then
  nRules=10,50,100,1000,5000

else
  nRules=100
fi


# output folder will be relative to the shared_volume,
# because in case of using docker we will need to output the stats outside the container
if [ "$mode" = "0" ] || [ "$mode" = "inf" ]; then
        mode="inf"
        outputFolder="${shared_volume}/output_${mode}_sparql${sparql}_ultimate${ultimate}/"
else
        if [ "$mode" = "1" ] || [ "$mode" = "eval" ]; then
            mode="eval"
            outputFolder="${shared_volume}/output_${mode}_${evalMode}/"
        else
          if [ "$mode" = "2" ] || [ "$mode" = "confl" ]; then
              mode="confl"
              outputFolder="${shared_volume}/output_${mode}_imdb/"
          fi
        fi
fi

if [ $use_docker = true ]; then

  image_name="shaclcompl/tests:latest"

  if [ $download_image = true ]; then
    docker pull $image_name
  else
    docker build -t=$image_name .
  fi

  sleep 20

  cont_name="run_${mode}"

  docker run --rm --name=${cont_name} -v ${shared_volume}/:${shared_volume} -it ${image_name} --mode=${mode}  --outputFolder=${outputFolder} --sparql="${sparql}" --evalMode=${evalMode} --ultimate="${ultimate}" --seeds="${seeds}" --policySizes="${policySizes}" --nRules="${nRules}"
  #sleep 3


  #sudo docker logs --follow ${cont_name} > ${cont_name}.log &
  #
  #echo "${cont_name}.log"
else
  java -cp shacl-compl-1.0-SNAPSHOT.jar.original SAVETests --mode=${mode}  --outputFolder=${outputFolder} --sparql="${sparql}" --evalMode=${evalMode} --ultimate="${ultimate}" --seeds="${seeds}"  --policySizes="${policySizes}" --nRules="${nRules}"
fi
