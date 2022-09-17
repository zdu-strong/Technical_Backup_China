import * as React from 'react';
import Slide from '@mui/material/Slide';
import { TransitionProps } from '@mui/material/transitions';

export const MessageMoreActionTranslation = React.forwardRef(function Transition(
  props: TransitionProps & {
    children: React.ReactElement;
  },
  ref: React.Ref<unknown>,
) {
  return <Slide direction="up" ref={ref} {...props} />;
});