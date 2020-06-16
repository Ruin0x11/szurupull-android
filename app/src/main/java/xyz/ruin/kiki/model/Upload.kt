package xyz.ruin.kiki.model

data class Upload(val alreadyUploaded: Boolean,
                  val source: String,
                  val tags: ArrayList<Tag>,
                  val url: String,
                  val previewUrl: String,
                  val safety: String,
                  val version: Int)