package vector_add_rocc

import chisel3._
import chisel3.util._
import org.chipsalliance.cde.config._
import freechips.rocketchip.tile._
import freechips.rocketchip.diplomacy._
import freechips.rocketchip.rocket._
import freechips.rocketchip.tilelink._


class VectorAddAccelerator(opcodes: OpcodeSet)
    (implicit p: Parameters) extends LazyRoCC(opcodes) {
  override lazy val module = new VectorAddAcceleratorModule(this)
}

class VectorAddAcceleratorModule(outer: VectorAddAccelerator)
    extends LazyRoCCModuleImp(outer) {

  val vectorAdd = Module(new VectorAdd())

  io.cmd <> vectorAdd.io.cmd
  io.resp <> vectorAdd.io.resp
  io.busy <> vectorAdd.io.busy   

}