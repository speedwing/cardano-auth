import io.gimbalabs.cardano.auth.v0.models.SignatureType

package object model {
  case class User(publicKey: String, signatureType: SignatureType)
}
