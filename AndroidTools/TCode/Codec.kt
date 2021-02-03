interface IDecoder: Runnable {
    fun pause();
    fun goOn();
    fun stop();
    fun isDecoding(): Boolean
    fun isSeeking(): Boolean
    fun isStop(): Boolean

    fun setStateListener(l: IDecoderStateListener?)
    fun getWidth(): Int
    fun getHeight()：Int
    fun getDuration(): Long
    fun getRotationAngle(): Int
    fun getMediaFormat(): MediaFormat?
    fun getTrack(): Int 
    fun getFilePath(): String 
}

abstract class BaseDecoder: IDecoder {
    private var mIsRunning = true
    private var mLock = Object()
    private var mReadyForDecode = false

    protected var mCodec: MediaCodec? = null
    protected var mExtractor: IExtractor ? = null
    protected var mInputBuffers: Array<ByteBuffer>? = null
    protected var mOutputBuffers: Array<ByteBuffer>? = null

    private var mBufferInfo = MediaCodec.BufferInfo()
    private var mState = DecodeState.STOP 
    private var mStateListener: IDecoderStateListener? =null
    
    private var mIsEOS = false
    protected var mVideoWidth = 0
    protected var mViedeoHeight = 0

//new 
    final override fun run(){
        mState = DecodeState.START
        mStateListener?.decoderPrepare(this)

        //Step1. init and start Codec
        if(!init()) return
        while (mIsRunning) {
            if (mState != DecodeState.START &&
                mState != DecodeState.DECODING &&
                mState !=DecodeState.SEEKING ) {
                    waitDecode()
                }
            
            if (!mIsRunning ||
                mState == DecodeState.STOP ) {
                mIsRunning = false
                break
            }

            if (!mIsEOS) {
                //Step2. push data to DeCode
                mIsEOS = pushBufferToDecoder()
            }

            //Step3. pull the Codec data form buffer
            var index = pullBufferFromDevocer()
            if (index >= 0) {
                //Step4. render
                render(mOutputBuffers!![index], mBufferInfo)
                //Step5. release buffer
                mCodec!!.releaseOUtputBuffer(index,true)
                if (mState == DecodeState.START){
                    mState = DecodeState.PAUSE
                }
            }

            //Step6. Finished?
            if (mBufferInfo.flags == MediaCodec.BUFFER_FLAG_END_OF_STREAM) {
                mState = DecodeState.FINISH
                mStateListener?.decoderFinish(this)
            }
        }

        doneDecode()
        //Step7. release CODEC
        release()
    }


    //CODEC waiting Thread
    private fun waitDecode() {
        tyr {
            if (mState == DecodeState.PAUSE) {
                mStateListener?.decoderPause(this)
            }
            synchronized(mLock){
                mLock.wait()
            }    
        } catch (e :Exception) {
            e.printStackTrace() 
        }
    }

    //Notify Thread going run 
    protected fun notifyDecode() {
        synchronized(mLock) {
            mLock.notifyAll()
        }
        if (mState == DecodeState.DECODING) {
            mStateListener?.decoderRunning(this)
        }
    }

    //Render
    abstract fun render(outputBuffers: ByteBuffer,
                        bufferInfo: MediaCodec.BufferInfo)

    //end Codec
    abstract fun doneDecode()

    private fun init(): Boolean {
        //check argc
        if (mFilePath.isEmpty() || File(mFilePath).exists()) {
            Log.w(TAG,"File Path if empty")
            mStateListener?.decoderError(this, "File path is empty")
            return false
        }

        if (!check()) return false

        mExtractor = initExtractor(mFilePath)
        if (mExtractor == null ||
            mExtractor!!.getFormat() == null) return false

        if(!initParams()) return false
        if(!initRender()) return false
        if(!initCodec()) return false
        return true
    }

   private fun initCodec(): Boolean {
       try{
            val type = mExtractor!!.getFormat()!!.getString(MediaFormat.KEY_MIME)
            mCodec = MediaCodec.createDecoderByType(type)

            if(!configCodec(mCodec!!,mExtracotr!!.getFormat()!!)){
                waitDecoe()
            }

            mCodec!!.start()

            mInputBuffers = mCodec?.mInputBuffers
            mOutputBuffers = mCodec?.mOutputBuffers
       }catch (e: Exception){
           retrun false
       }
       return true
   } 

   private fun pushBufferToDecoder(): Boolean {
       var mInputBufferIndex = mCodec!!.dequeueInputBuffer(2000)
       var isEndOfStream = false

       if (inputBufferIndex >= 0) {
           val inputBuffer = mInputBuffers!![inputBufferIndex]
           val sampleSize = mExtracotr!!.readBuffer(inputBuffer)
           if(sampleSize < 0) {
               mCodec!!.queueInputBuffer(inputBufferIndex, 0, 0, 0, MediaCodec.BUFFER_FLAG_END_OF_STREAM)
               isEndOfStream = true
           } else {
               mCodec!!.queueInputBuffer(inputBufferIndex, 0, sampleSize, mExtracotr!!.getCurrentTimestamp(), 0)
           }
           
       }
       return isEndOfStream
   }

   private fun pullBufferFromDecoder(): Int {
       var index = mCodec!!.dequequeOutputBuffer(mBufferInfo, 1000)
       when (index) {
           MediaCodec.INFO_OUTPUT_FORMAT_CHANGED -> {}
           MediaCodec.INFO_TRY_AGAIN_LATER -> {}
           MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED -> {
               mOutputBuffers = mCodec!!.outputBuffers
           }
           else -> {
               return index
           }
           return -1
       }
   }


   abstract fun check(): Boolean
   abstract fun initExtractor(path: String): IExtractor
   abstract fun initSpecParams(format: MediaFormat)
   abstract fun initRender(): Boolean
   abstract fun configCodec(codec: MediaCodec, format: MediaFormat): Boolean


}

enum class DecodeState {
    START,
    DECODING,
    PAUSE,
    SEEKING,
    FINISH,
    STOP
}


interface IExtractor {
    fun getFormat(): MediaFormat?
    fun readBuffer(bytBuffer: ByteBuffer): Int
    fun getCurrentTimestamp(): Long
    fun seek(pos: Long): Long
    fun setStartPos(pos: Lone)
    fun stop()
}

