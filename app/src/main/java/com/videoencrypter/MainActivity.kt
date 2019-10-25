package com.videoencrypter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.daniel.appbase.components.EmptyLoadingViewPod
import com.daniel.appbase.showLogD
import com.daniel.appbase.toast
import com.daniel.encrypter.FetchPath.getPath
import com.daniel.encrypter.FileEncrypter
import com.daniel.encrypter.FileListEncrypter
import com.daniel.encrypter.FileListEncryptionListener
import com.daniel.encrypter.FileUtils
import com.videoencrypter.adapters.MainAdapter
import com.videoencrypter.delegates.VideoDelegate
import com.videoencrypter.vos.BaseVideoListVO
import com.videoencrypter.vos.VideoListVO
import com.videoencrypter.vos.VideoPreviewVO
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.File


class MainActivity : AppCompatActivity(), VideoDelegate, EmptyLoadingViewPod.OnRefreshListener,
    FileListEncryptionListener {
    override fun onStartEncryptingFilesList() {
        toastOnUI("Encrypting Started")
    }

    override fun onStartDecryptingFilesList() {
        toastOnUI("Decrypting Started")
    }

    override fun onFinishedEncryptingFilesList() {
        toastOnUI("Encrypting Ended")
        runOnUiThread {
            val path =
                Environment.getExternalStorageDirectory().path + File.separator + ENCRYPTED_DIRECTORY
            val fileList = FileUtils.readFilesFromDirectory(path)
            GlobalScope.launch {
                if (fileList != null)
                    FileListEncrypter.getInstance().decryptFiles(fileList)
            }
        }
    }

    override fun onFinishedDecryptingFilesList() {
        toastOnUI("Decrypting Ended")
    }

    override fun onProgressUpdate(percentage: Int) {

    }

    override fun onCancelled(msg: String) {
        toastOnUI(msg)
    }

    /*  override fun onStartEncryption(msg: String) {
          toastOnUI(msg)
      }

      override fun onFinishedEncryption(msg: String) {
          toastOnUI(msg)
      }

      override fun onStartDecryption(msg: String) {
          toastOnUI(msg)
      }

      override fun onFinishedDecryption(msg: String) {
          toastOnUI(msg)
      }

      override fun onStartWritingFile(msg: String) {
          toastOnUI(msg)
      }

      override fun onFinished() {
  //        runOnUiThread {
  //            if (!hasDecryptedFile) {
  //                hasDecryptedFile = true
  //                val file = File(
  //                    Environment.getExternalStorageDirectory()
  //                        .absolutePath + File.separator + "Android/data/com.videoencrypter/encrypted/",
  //                    "$fileName-enc.$fileExtension"
  //                )
  //
  //
  //                val decryptTask =
  //                    GlobalScope.async {
  //                        FileEncrypter.getInstance()
  //                            .decryptFile(file, "${fileName}-dec.$fileExtension")
  //                    }
  //                runBlocking {
  //                    decryptTask.await()
  //                }
  //
  //            } else {
  //                toastOnUI("Finished ENDE Opts")
  //                readVideoList()
  //            }
  //        }
      }

      override fun onFailedWritingFile(msg: String) {
          toast(msg)
      }*/

    override fun onRefreshButtonClicked() {
        initVideoCursor()
//        readVideoList()
        readFileList()
    }


    override fun encryptVideo(uri: Uri) {

    }

    override fun decryptVideo(uri: Uri) {

    }

    private val ENCRYPTED_DIRECTORY = "Android/data/com.videoencrypter/encrypted/"
    private val DECRYPTED_DIRECTORY = "Android/data/com.videoencrypter/videos"
    private var key: String = "123456789"
    private var ivs: String = "123456789"
    private var hasDecryptedFile = false
    private val REQUEST_TAKE_GALLERY_VIDEO = 1
    private var videoCursor: Cursor? = null
    private lateinit var adapter: MainAdapter
    private var fileName = ""
    private var fileExtension = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        createAppOwnDirectory()

        FileListEncrypter.Builder()
            .withEncryptDirectory(ENCRYPTED_DIRECTORY)
            .withDecryptDirectory(DECRYPTED_DIRECTORY)
            .withEncryptionListener(this)
            .withKey(key)
            .withIvs(ivs)
            .withMode(FileEncrypter.MODE.AESBF)
            .build()

        adapter = MainAdapter(this)
        videoPlayListRv.adapter = adapter
        videoPlayListRv.layoutManager = LinearLayoutManager(this)
        videoPlayListRv.setEmptyView(emptyViewVideoPlayList as EmptyLoadingViewPod)
        (emptyViewVideoPlayList as EmptyLoadingViewPod).setOnRefreshListener(this)
        onRefreshButtonClicked()

        encryptFileBtn.setOnClickListener {
            pickVideoFileAndEncryptIt()
        }
    }

    private fun pickVideoFileAndEncryptIt() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, REQUEST_TAKE_GALLERY_VIDEO)
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_TAKE_GALLERY_VIDEO) {
                val selectedImageUri = data!!.data

                showLogD("uri $selectedImageUri")
                val file = File(getPath(this, selectedImageUri!!)!!)

                fileName = file.name
                fileExtension = file.extension
                val encryptTask = GlobalScope.async {
                    FileEncrypter.getInstance()
                        .encryptFile(file, "${fileName}-enc.${fileExtension}")
                }

                runBlocking {
                    encryptTask.await()
                }
            }
        }
    }


    private fun createAppOwnDirectory() {
        val mydir =
            this.getDir("com.videoencrypter", Context.MODE_PRIVATE) //Creating an internal dir;

        showLogD("${Environment.getExternalStorageDirectory().path}/Android/data/com.videoencrypter")
        val videoDir =
            File(
                "${Environment.getExternalStorageDirectory().path}/Android/data/com.videoencrypter",
                "videos"
            ) //Getting a file within the dir.
        val encryptedDir = File(
            "${Environment.getExternalStorageDirectory().path}/Android/data/com.videoencrypter",
            "encrypted"
        )

        if (!videoDir.exists()) {
            if (!videoDir.mkdirs()) {
                showLogD("failed to create directory")
            }
        }

        if (!encryptedDir.exists()) {
            if (!encryptedDir.mkdirs()) {
                showLogD("failed to create directory")
            }
        }
    }


    private fun initVideoCursor() {
        val selection = MediaStore.Video.Media.DATA + " like?"
//        val selection = arrayOf(MediaStore.Video.VideoColumns.DATA)
        val selectionArgs = arrayOf("%$DECRYPTED_DIRECTORY%")
        val parameters = arrayOf(
            MediaStore.Video.Media._ID,
            MediaStore.Video.Media.DATA,
            MediaStore.Video.Media.DISPLAY_NAME,
            MediaStore.Video.Media.DATE_MODIFIED,
            MediaStore.Video.Media.SIZE

        )
        videoCursor = managedQuery(
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
            parameters, selection, selectionArgs, MediaStore.Video.Media.DATE_TAKEN + " DESC"
        )

    }

    private fun readVideos() {
        val videoList = ArrayList<VideoPreviewVO>()
        MediaScannerConnection.scanFile(
            this,
            arrayOf(Environment.getExternalStorageDirectory().absolutePath + File.separator + "x"),
            null
        ) { path, uri ->
            //Do something
            val file = File(path)
            videoList.add(
                VideoPreviewVO(
                    isEncrypted = false,
                    uri = uri,
                    videoName = file.name,
                    size = "Unknown",
                    dateCreatedAt = "Unknown"
                )
            )
        }
        val normalVideoListData = VideoListVO(videoList)
        val data = ArrayList<BaseVideoListVO>()
        data.add(normalVideoListData)
        showLogD(data.toString())
        adapter.appendNewData(data)
    }

    private fun readFileList() {
        adapter.clearData()
        val path = "${Environment.getExternalStorageDirectory().path}/Testing"
        val fileList = FileUtils.readFilesFromDirectory(path)
        val videoList = ArrayList<VideoPreviewVO>()
        if (fileList != null) {
            for (file in fileList) {
                val video = VideoPreviewVO(
                    isEncrypted = false,
                    uri = Uri.parse(file.path),
                    videoName = file.name,
                    size = file.length().toString(),
                    dateCreatedAt = ""
                )
                videoList.add(video)
            }


            encryptFiles(fileList)


            val normalVideoListData = VideoListVO(videoList)
            val data = ArrayList<BaseVideoListVO>()
            data.add(normalVideoListData)
            showLogD(data.toString())
            adapter.appendNewData(data)
        }
    }

    private fun encryptFiles(fileList: Array<File>) {
        GlobalScope.launch {


            FileListEncrypter.getInstance()
                .encryptFiles(fileList)
        }
    }

    override fun onPause() {
        super.onPause()
        FileUtils.deleteDir(Environment.getExternalStorageDirectory().path + File.separator + DECRYPTED_DIRECTORY)
    }

    override fun onStop() {
        super.onStop()
        GlobalScope.launch {

        }
    }


    private fun getThumbnails(id: Long): Bitmap? {
        val options = BitmapFactory.Options()
        options.inSampleSize = 1
        return MediaStore.Video.Thumbnails.getThumbnail(
            contentResolver,
            id,
            MediaStore.Video.Thumbnails.MICRO_KIND,
            options
        )
    }

    @Throws(Throwable::class)
    fun retriveVideoFrameFromVideo(videoPath: String): Bitmap? {
        val bitmap: Bitmap?
        var mediaMetadataRetriever: MediaMetadataRetriever? = null
        try {
            mediaMetadataRetriever = MediaMetadataRetriever()
//            if (SDK_INT >= 14)
            mediaMetadataRetriever.setDataSource(videoPath, HashMap())
            /*else
                mediaMetadataRetriever.setDataSource(videoPath)
*/
            bitmap = mediaMetadataRetriever.getFrameAtTime(1, MediaMetadataRetriever.OPTION_CLOSEST)
        } catch (e: Exception) {
            e.printStackTrace()
            throw Throwable(
                "Exception in retriveVideoFrameFromVideo(String videoPath)" + e.message
            )
        } finally {
            mediaMetadataRetriever?.release()
        }
        return bitmap
    }

    private fun toastOnUI(msg: String) =
        runOnUiThread {
            toast(
                msg
            )
        }


}
