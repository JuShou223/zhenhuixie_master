import mitt from 'mitt'

export type Events = {
  'refreshIndex': void
  [key: string]: unknown
}

const bus = mitt<Events>()
export default bus
