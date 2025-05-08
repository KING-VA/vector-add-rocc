package vector_add_rocc

import chisel3._
import chisel3.util._
import freechips.rocketchip.tile._
import org.chipsalliance.cde.config._
import freechips.rocketchip.diplomacy._

class WithVecAddRoCC extends Config((site, here, up) => {
  case BuildRoCC => up(BuildRoCC) ++ Seq((p: Parameters) => {
    val VecAddRoCC = LazyModule(new VectorAddAccelerator(OpcodeSet.custom1)(p))
    VecAddRoCC
  })
})