<template>
  <ul>
    <li>
      <span
          v-html="convertMarkDown(questionDetails.answer)"
      />
    </li>
  </ul>
</template>

<script lang="ts">
import { Component, Vue, Prop } from 'vue-property-decorator';
import { convertMarkDown } from '@/services/ConvertMarkdownService';
import Image from '@/models/management/Image';
import OpenQuestionDetails from '@/models/management/questions/OpenQuestionDetails';

@Component
export default class OpenQuestionView extends Vue {
  @Prop() readonly questionDetails!: OpenQuestionDetails;

  convertMarkDown(text: string, image: Image | null = null): string {
    return convertMarkDown(text, image);
  }
}
</script>
