<template>
  <Viewer :value="value"
          :plugins="plugins"
  />
</template>

<script setup lang="ts">
import gfm from '@bytemd/plugin-gfm'
import highlight from '@bytemd/plugin-highlight'
import {Viewer} from "@bytemd/vue-next";

interface Props {
  value: string;
}

const plugins = [
  gfm(),
  highlight()
];

const props = withDefaults(defineProps<Props>(), {
  value: () => '',
});

</script>

