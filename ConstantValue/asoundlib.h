#ifndef ASOUNDLIB_H
#define ASOUNDLIB_H

#if defined(__cplusplsu)
extern "C"{
#endif

/* 
* PCM API 
*/

struct pcm;

#define PCM_OUT         0x00000000
#define PCM_IN          0x10000000
#define PCM_MMAP        0x00000001
#define PCM_NOIRQ       0x00000002/*  */
#define PCM_NORESTART   0x00000004
#define PCM_MONOTONIC   0x00000008

/* PCM runtime states */
#define PCM_STATE_OPEN          0
#define PCM_STATE_SETUP         1
#define PCM_STATE_PREPARED      2
#define PCM_STATE_RUNNING       3
#define PCM_STATE_XRUN          4
#define PCM_STATE_DRAINING      5
#define PCM_STATE_PAUSE         6
#define PCM_STATE_SUSPENDED     7
#define PCM_STATE_DISCONNECT    8

/* TLV header size */
#define TLV_HEADER_SIZE (2 * sizeof(unsigned int))

/* Bit formats */
enum pcm_format {
    PCM_FORMAT_INVALID = -1,
    PCM_FORMAT_S16_LE = 0,
    PCM_FORMAT_S32_LE ,
    PCM_FORMAT_S8 ,
    PCM_FORMAT_S24_LE,
    PCM_FORMAT_S32_3LE,

    PCM_FORMAT_MAX,
}

/* Bitmask has 256bits (32 bytes) in asound.h */
struct pcm_mask {
    unsigned int bits[32 / sizeof(unsigned int)];
}

/* Configuration for a stream */
struct pcm_config {
    unsigned int channels;
    unsigned int rate;
    unsigned int period_size;
    unsigned int period_count;

    enum pcm_format format;

    /* 
    * start_threshold   : period_count * period_size
    * stop_threshold    : period_count * period_size
    */
    unsigned int start_threshold;
    unsigned int stop_threshold;
    unsigned int silence_threshold;
    unsigned int silence_size;

    int avail_min;

}

enum pcm_param
{
    /* mask parameters */
    PCM_PARAM_ACCESS,
    PCM_PARAM_FORMAT,
    PCM_PARAM_SUBFORMAT,
    /* interval parameters */
    PCM_PARAM_SAMPLE_BITS,
    PCM_PARAM_FRAME_BITS,
    PCM_PARAM_CHANNELS,
    PCM_PARAM_RATE,
    PCM_PARAM_PERIOD_TIME,
    PCM_PARAM_PERIOD_SIZE,
    PCM_PARAM_PERIOD_BYTES,
    PCM_PARAM_PERIODS,
    PCM_PARAM_BUFFER_TIME,
    PCM_PARAM_BUFFER_SIZE,
    PCM_PARAM_BUFFER_BYTES,
    PCM_PARAM_TICK_TIME,
};

/* Mixer control types */
enum mixer_ctl_type {
    MIX_CTL_TYPE_BOOL,
    MIX_CTL_TYPE_INT,
    MIX_CTL_TYPE_ENUM,
    MIX_CTL_TYPE_BYTE,
    MIX_CTL_TYPE_IEC958,
    MIX_CTL_TYPE_INT64,
    MIX_CTL_TYPE_UNKNOW,

    MIX_CTL_TYPE_MAX,
}

/* Open and close a stream */
struct pcm *pcm_open(unsigned int card, unsigned int device, unsigned int flags, struct pcm_config *config);
int pcm_close(struct pcm *pcm);
int pcm_is_ready(struct pcm *pcm);

/* Obtain the parameters for a PCM */
struct pcm_params *pcm_params_get(unsigned int card, unsigned int device, unsigned int flags);
void pcm_params_free(struct pcm_params *pcm_params);
struct pcm_mask *pcm_params_get_mask(struct pcm_params *pcm_params, enum pcm_param params);
unsigned int pcm_params_get_min(struct pcm_params *pcm_params, enum pcm_param params);
void pcm_params_set_min(struct pcm_params *pcm_params, enum pcm_param params, unsigned int val);
unsigned int pcm_params_get_max(struct pcm_params *pcm_params, enum pcm_param params);
void pcm_params_set_max(struct pcm_params *pcm_params, enum pcm_param params, unsigned int val);

int pcm_params_to_string(struct pcm_params *params, char *string, unsigned int size);
int pcm_params_format_test(struct pcm_params *params, enum pcm_format format);

/* Set and get config */
int pcm_get_config(struct pcm *pcm, struct pcm_config *config);
int pcm_set_config(struct pcm *pcm, struct pcm_config *config);

/* Returns a human readable reason for the last error */
const char *pcm_get_error(struct pcm *pcm);

unsigned int pcm_format_to_bits(enum pcm_format format);

/* Returns the buffer size (int frames) that should be used for pcm_write. */
unsigned int pcm_get_buffer_size(struct pcm *pcm);
unsigned int pcm_frames_to_bytes(struct pcm *pcm, unsigned int frames);
unsigned int pcm_bytes_to_frames(struct pcm *pcm, unsigned int bytes);

/* Returns the pcm latency in ms */
unsigned int pcm_get_latency(struct pcm *pcm);

int pcm_get_htimestamp(struct pcm *pcm, unsigned int *avail,
                       struct timespec *tstamp);

/* Returns the subdevice on which the pcm has been opened */
unsigned int pcm_get_subdevice(struct pcm *pcm);

/* Write data to the fifo.
 * Will start playback on the first write or on a write that
 * occurs after a fifo underrun.
 */
int pcm_write(struct pcm *pcm, const void *data, unsigned int count);
int pcm_read(struct pcm *pcm, void *data, unsigned int count);


/*
 * mmap() support.
 */
int pcm_mmap_write(struct pcm *pcm, const void *data, unsigned int count);
int pcm_mmap_read(struct pcm *pcm, void *data, unsigned int count);
int pcm_mmap_begin(struct pcm *pcm, void **areas, unsigned int *offset,
                   unsigned int *frames);
int pcm_mmap_commit(struct pcm *pcm, unsigned int offset, unsigned int frames);
int pcm_mmap_avail(struct pcm *pcm);

/* Returns current read/write position in the mmap buffer with associated time stamp.
 */
int pcm_mmap_get_hw_ptr(struct pcm* pcm, unsigned int *hw_ptr, struct timespec *tstamp);

/* Prepare the PCM substream to be triggerable */
int pcm_prepare(struct pcm *pcm);
/* Start and stop a PCM channel that doesn't transfer data */
int pcm_start(struct pcm *pcm);
int pcm_stop(struct pcm *pcm);

/* ioctl function for PCM driver */
int pcm_ioctl(struct pcm *pcm, int request, ...);

/* Interrupt driven API */
int pcm_wait(struct pcm *pcm, int timeout);
int pcm_get_poll_fd(struct pcm *pcm);

/* Change avail_min after the stream has been opened with no need to stop the stream.
 * Only accepted if opened with PCM_MMAP and PCM_NOIRQ flags
 */
int pcm_set_avail_min(struct pcm *pcm, int avail_min);


*
 * MIXER API
 */

struct mixer;
struct mixer_ctl;

/* Open and close a mixer */
struct mixer *mixer_open(unsigned int card);
void mixer_close(struct mixer *mixer);

/* Get info about a mixer */
const char *mixer_get_name(struct mixer *mixer);

/* Obtain mixer controls */
unsigned int mixer_get_num_ctls(struct mixer *mixer);
struct mixer_ctl *mixer_get_ctl(struct mixer *mixer, unsigned int id);
struct mixer_ctl *mixer_get_ctl_by_name(struct mixer *mixer, const char *name);

/* Get info about mixer controls */
const char *mixer_ctl_get_name(struct mixer_ctl *ctl);
enum mixer_ctl_type mixer_ctl_get_type(struct mixer_ctl *ctl);
const char *mixer_ctl_get_type_string(struct mixer_ctl *ctl);
unsigned int mixer_ctl_get_num_values(struct mixer_ctl *ctl);
unsigned int mixer_ctl_get_num_enums(struct mixer_ctl *ctl);
const char *mixer_ctl_get_enum_string(struct mixer_ctl *ctl, unsigned int enum_id);

/* Some sound cards update their controls due to external events,
 * such as HDMI EDID byte data changing when an HDMI cable is
 * connected. This API allows the count of elements to be updated.
 */
void mixer_ctl_update(struct mixer_ctl *ctl);

/* Set and get mixer controls */
int mixer_ctl_get_percent(struct mixer_ctl *ctl, unsigned int id);
int mixer_ctl_set_percent(struct mixer_ctl *ctl, unsigned int id, int percent);

int mixer_ctl_get_value(struct mixer_ctl *ctl, unsigned int id);
int mixer_ctl_is_access_tlv_rw(struct mixer_ctl *ctl);
int mixer_ctl_get_array(struct mixer_ctl *ctl, void *array, size_t count);
int mixer_ctl_set_value(struct mixer_ctl *ctl, unsigned int id, int value);
int mixer_ctl_set_array(struct mixer_ctl *ctl, const void *array, size_t count);
int mixer_ctl_set_enum_by_string(struct mixer_ctl *ctl, const char *string);

/* Determine range of integer mixer controls */
int mixer_ctl_get_range_min(struct mixer_ctl *ctl);
int mixer_ctl_get_range_max(struct mixer_ctl *ctl);

int mixer_subscribe_events(struct mixer *mixer, int subscribe);
int mixer_wait_event(struct mixer *mixer, int timeout);
int mixer_consume_event(struct mixer *mixer);



#if defined(__cplusplsu)
} /* extern "C" */
#endif

#endif